/*
 * FormalParameter.java
 */

package jltools.ast;

import jltools.types.Type;

/**
 * Overview: A FormalParameter is mutable representation of a pair of
 * values, a type and a variable declarator id, used as formal
 * parameters such as in method declarations and catch blocks.
 */

public class FormalParameter {
    
    /**
     * Effects: Creates a new FormalParameter of type <type> bound to
     * the name <name>.  The FormalParameter is final if <isFinal> is
     * true.
     */
    public FormalParameter (TypeNode type, String name, boolean isFinal) {
	this.type = type;
	this.name = name;
	this.isFinal = isFinal;
    }

    /**
     * Effects: Creates a new FormalParameter of type <type> bound to
     * the name <name>.  The FormalParameter is final if <isFinal> is
     * true.
     */
    public FormalParameter (Type type, String name, boolean isFinal) {
	this.type = new TypeNode(type);
	this.name = name;
	this.isFinal = isFinal;
    }

    /**
     * Effects: Returns the type of this parameter.
     */
    public TypeNode getType() {
	return type;
    }

    /**
     * Effects: Sets the type of this FormalParameter to be <newType>.
     */
    public void setType(TypeNode newType) {
	type = newType;
    }

    /**
     * Effects: Sets the type of this FormalParameter to be <newType>.
     */
    public void setType(Type newType) {
	type = new TypeNode(newType);
    }

    /**
     * Effects: Returns the name of this FormalParameter.
     */
    public String getName() {
	return name;
    }

    /**
     * Effects: Sets the name of this FormalParameter to be newName.
     */
    public void setName(String newName) {
	name = newName;
    }

    /**
     * Effects: returns true if this FormalParameter is final.
     */
    public boolean isFinal() {
	return isFinal;
    }

    /**
     * Effects: if <isFinal> is true, sets this FormalParameter to be
     * final.  If <isFinal> is false, sets this FormalParameter to not
     * be final.
     */
    public void setFinal(boolean isFinal) {
	this.isFinal = isFinal;
    }

    public FormalParameter deepCopy() {
      return new FormalParameter((TypeNode) type.deepCopy(),
				 name,
				 isFinal);
    }

    private TypeNode type;
    private String name;
    private boolean isFinal;
}
