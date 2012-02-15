package polyglot.ext.jl5.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.*;
import polyglot.ext.jl5.types.JL5ParsedClassType;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.ext.jl5.visit.JL5Translator;
import polyglot.types.Context;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

public class JL5Call_c extends Call_c implements JL5Call {

    private List<TypeNode> typeArgs;

    public JL5Call_c(Position pos, Receiver target, List typeArgs, Id name, List arguments) {
        super(pos, target, name, arguments);
        this.typeArgs = typeArgs;
    } 
//    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
//        JL5Call n = (JL5Call)super.disambiguate(ar);
//        for (TypeNode tn : n.typeArgs()) {
//            if (!tn.isDisambiguated()) {
//                return n;
//            }
//        }
//        // types are disambiguated
//        // apply them to the method instance if needed.
//        JL5TypeSystem ts = ar.typeSystem();
//        ts.instantiateMethodInstance();
//        return n;
//    }

    @Override
    public List<TypeNode> typeArgs() {
        return this.typeArgs;
    }

    @Override
    public JL5Call typeArgs(List<TypeNode> typeArgs) {
        if (this.typeArgs == typeArgs) {
            return this;
        }
        JL5Call_c n = (JL5Call_c)this.copy();
        n.typeArgs = typeArgs;
        return n;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        JL5Call_c n = (JL5Call_c)super.visitChildren(v);
        List targs = visitList(n.typeArgs, v);
        return n.typeArgs(targs);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        Context c = tc.context();

        List argTypes = new ArrayList(this.arguments.size());

        for (Iterator i = this.arguments.iterator(); i.hasNext(); ) {
            Expr e = (Expr) i.next();
            if (! e.type().isCanonical()) {
                return this;
            }
            argTypes.add(e.type());
        }

        if (this.target == null) {
            return this.typeCheckNullTarget(tc, argTypes);
        }
               
        if (! this.target.type().isCanonical()) {
            return this;
        }
        List actualTypeArgs = new ArrayList(this.typeArgs.size());
        for (TypeNode tn : this.typeArgs) {
            actualTypeArgs.add(tn.type());
        }
                
        ReferenceType targetType = this.findTargetType();
        
        // SC: semi correct fix for dealing with raw types?
        if (this.target != null && !(this.target instanceof Special) && targetType instanceof JL5ParsedClassType) {
            if (!ts.typeEquals(targetType, tc.context().currentClass())) {
                // it's a raw type of some other class.
                // use its erasure subst
                targetType = (ReferenceType) ts.erasureType(targetType);
            }
        }

        MethodInstance mi = ts.findMethod(targetType, 
                                          this.name.id(), 
                                          argTypes, 
                                          actualTypeArgs,
                                          c.currentClass());
        
        /* This call is in a static context if and only if
         * the target (possibly implicit) is a type node.
         */
        boolean staticContext = (this.target instanceof TypeNode);

        if (staticContext && !mi.flags().isStatic()) {
            throw new SemanticException("Cannot call non-static method " + this.name.id()
                                  + " of " + target.type() + " in static "
                                  + "context.", this.position());
        }

        // If the target is super, but the method is abstract, then complain.
        if (this.target instanceof Special && 
            ((Special)this.target).kind() == Special.SUPER &&
            mi.flags().isAbstract()) {
                throw new SemanticException("Cannot call an abstract method " +
                               "of the super class", this.position());            
        }

        JL5Call_c call = (JL5Call_c)this.methodInstance(mi).type(mi.returnType());

//        System.err.println("JL5Call_c: " + this + " got mi " + mi);
        
        // If we found a method, the call must type check, so no need to check
        // the arguments here.
        call.checkConsistency(c);

        return call;
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (!targetImplicit) {
            if (target instanceof Expr) {
                printSubExpr((Expr) target, w, tr);
            }
            else if (target != null) {
                if (tr instanceof JL5Translator) {
                    JL5Translator jltr = (JL5Translator)tr;
                    jltr.printReceiver(target, w);                    
                }
                else {
                    print(target, w, tr);
                }
            }
            w.write(".");
            w.allowBreak(2, 3, "", 0);
        }

        w.begin(0);
        w.write(name + "(");
        if (arguments.size() > 0) {
            w.allowBreak(2, 2, "", 0); // miser mode
            w.begin(0);

            for(Iterator i = arguments.iterator(); i.hasNext();) {
                Expr e = (Expr) i.next();
                print(e, w, tr);

                if (i.hasNext()) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
            }

            w.end();
        }
        w.write(")");
        w.end();
    }


}
