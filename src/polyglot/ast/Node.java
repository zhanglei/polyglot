package polyglot.ast;

import polyglot.util.Copy;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Type;
import polyglot.visit.*;

/**
 * A <code>Node</code> represents an AST node.  All AST nodes must implement
 * this interface.  Nodes should be immutable: methods which set fields
 * of the node should copy the node, set the field in the copy, and then
 * return the copy.
 */
public interface Node extends NodeOps, Copy
{
    /**
     * Set the delegate of the node.
     */
    Node del(Del del);

    /**
     * Get the node's delegate.
     */
    Del del();

    /**
     * Get the position of the node in the source file.  Returns null if
     * the position is not set.
     */
    Position position();

    /** Create a copy of the node with a new position. */
    Node position(Position position);

    /**
     * Visit the node.  This method is equivalent to
     * <code>visitEdge(null, v)</code>.
     *
     * @param v The visitor which will traverse/rewrite the AST.
     * @return A new AST if a change was made, or <code>this</code>.
     */
    Node visit(NodeVisitor v);

    /**
     * Visit the node, passing in the node's parent.  This method is called by
     * a <code>NodeVisitor</code> to traverse the AST starting at this node.
     * This method should call the <code>override</code>, <code>enter</code>,
     * and <code>leave<code> methods of the visitor.  The method may return a
     * new version of the node.
     *
     * @param parent The parent of <code>this</code> in the AST.
     * @param v The visitor which will traverse/rewrite the AST.
     * @return A new AST if a change was made, or <code>this</code>.
     */
    Node visitEdge(Node parent, NodeVisitor v);

    /**
     * Visit the children of the node.
     *
     * @param v The visitor which will traverse/rewrite the AST.
     * @return A new AST if a change was made, or <code>this</code>.
     */
    // Node visitChildren(NodeVisitor v);

    /**
     * Visit a single child of the node.
     *
     * @param v The visitor which will traverse/rewrite the AST.
     * @param child The child to visit.
     * @return The result of <code>child.visit(v)</code>, or <code>null</code>
     * if <code>child</code> was <code>null</code>.
     */
    Node visitChild(Node child, NodeVisitor v);

    /**
     * Push a new scope for visiting children and add any declarations to the
     * new context that should be in scope when visiting children.
     * This should <i>not</i> update the old context imperatively.  Use
     * <code>addDecls</code> when leaving the node for that.
     */
    // Context enterScope(Context c);

    /**
     * Add any declarations to the context that should be in scope when
     * visiting later sibling nodes.
     */
    // void addDecls(Context c);

    /**
     * Get the expected type of a child expression of <code>this</code>.
     * The expected type is determined by the context in that the child occurs
     * (e.g., for <code>x = e</code>, the expected type of <code>e</code> is
     * the declared type of <code>x</code>.
     *
     * @param child A child expression of this node.
     * @param av An ascription visitor.
     * @return The expected type of <code>child</code>.
     */
    Type childExpectedType(Expr child, AscriptionVisitor av);

    /**
     * Dump the AST node for debugging purposes.
     */
    void dump(CodeWriter w);

    //////////////////////////////////////////////////////////////// 
    // Duplicate the NodeOps interface, but deprecate the methods.
    // That way, we'll get a warning if we try to call these directly.
    //////////////////////////////////////////////////////////////// 

    /** @deprectated */
    Node buildTypesOverride(TypeBuilder tb) throws SemanticException;
    /** @deprectated */
    NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException;
    /** @deprectated */
    Node buildTypes(TypeBuilder tb) throws SemanticException;
    /** @deprectated */
    Node disambiguateOverride(AmbiguityRemover ar) throws SemanticException;
    /** @deprectated */
    NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException;
    /** @deprectated */
    Node disambiguate(AmbiguityRemover ar) throws SemanticException;
    /** @deprectated */
    Node addMembersOverride(AddMemberVisitor am) throws SemanticException;
    /** @deprectated */
    NodeVisitor addMembersEnter(AddMemberVisitor am) throws SemanticException;
    /** @deprectated */
    Node addMembers(AddMemberVisitor am) throws SemanticException;
    /** @deprectated */
    Node foldConstantsOverride(ConstantFolder cf);
    /** @deprectated */
    NodeVisitor foldConstantsEnter(ConstantFolder cf);
    /** @deprectated */
    Node foldConstants(ConstantFolder cf);
    /** @deprectated */
    Node typeCheckOverride(TypeChecker tc) throws SemanticException;
    /** @deprectated */
    NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException;
    /** @deprectated */
    Node typeCheck(TypeChecker tc) throws SemanticException;
    /** @deprectated */
    Node exceptionCheckOverride(ExceptionChecker ec) throws SemanticException;
    /** @deprectated */
    NodeVisitor exceptionCheckEnter(ExceptionChecker ec) throws SemanticException;
    /** @deprectated */
    Node exceptionCheck(ExceptionChecker ec) throws SemanticException;
    /** @deprectated */
    void translate(CodeWriter w, Translator tr);
    /** @deprectated */
    void prettyPrint(CodeWriter w, PrettyPrinter pp);
}
