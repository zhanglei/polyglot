/*******************************************************************************
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2012 Polyglot project group, Cornell University
 * Copyright (c) 2006-2012 IBM Corporation
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * This program and the accompanying materials are made available under
 * the terms of the Lesser GNU Public License v2.0 which accompanies this
 * distribution.
 * 
 * The development of the Polyglot project has been supported by a
 * number of funding sources, including DARPA Contract F30602-99-1-0533,
 * monitored by USAF Rome Laboratory, ONR Grant N00014-01-1-0968, NSF
 * Grants CNS-0208642, CNS-0430161, and CCF-0133302, an Alfred P. Sloan
 * Research Fellowship, and an Intel Research Ph.D. Fellowship.
 *
 * See README for contributors.
 ******************************************************************************/

package polyglot.ast;

import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * A <code>BooleanLit</code> represents a boolean literal expression.
 */
public class BooleanLit_c extends Lit_c implements BooleanLit {
    protected boolean value;

    public BooleanLit_c(Position pos, boolean value) {
        super(pos);
        this.value = value;
    }

    /** Get the value of the expression. */
    @Override
    public boolean value() {
        return this.value;
    }

    /** Set the value of the expression. */
    @Override
    public BooleanLit value(boolean value) {
        BooleanLit_c n = (BooleanLit_c) copy();
        n.value = value;
        return n;
    }

    /** Type check the expression. */
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        return type(tc.typeSystem().Boolean());
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /** Write the expression to an output file. */
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write(String.valueOf(value));
    }

    /** Dumps the AST. */
    @Override
    public void dump(CodeWriter w) {
        super.dump(w);

        w.allowBreak(4, " ");
        w.begin(0);
        w.write("(value " + value + ")");
        w.end();
    }

    @Override
    public Object constantValue() {
        return Boolean.valueOf(value);
    }

    @Override
    public Node copy(NodeFactory nf) {
        return nf.BooleanLit(this.position, this.value);
    }

}
