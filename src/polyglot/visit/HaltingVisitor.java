/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.visit;

import polyglot.ast.Node;
import polyglot.util.*;
import java.util.*;

/**
 * A HaltingVisitor is used to prune the traversal of the AST at a
 * particular node.  Clients can call <code>bypass(Node n) </code> to 
 * have the visitor skip n and its children when recursing through the AST.
 */
public abstract class HaltingVisitor extends NodeVisitor
{
    protected Node bypassParent;
    protected Collection bypass;

    /** Return a new visitor that will bypass all children of node n. */ 
    public HaltingVisitor bypassChildren(Node n) {
        HaltingVisitor v = (HaltingVisitor) copy();
        v.bypassParent = n;
        return v;
    }

    /** Return a new visitor that will visit all children. */
    public HaltingVisitor visitChildren() {
        HaltingVisitor v = (HaltingVisitor) copy();
        v.bypassParent = null;
        v.bypass = null;
        return v;
    }

    /** Return a new visitor that bypasses node n during visit children. */
    public HaltingVisitor bypass(Node n) {
        if (n == null) return this;

        HaltingVisitor v = (HaltingVisitor) copy();

        // FIXME: Using a collection is expensive, but is hopefully not
        // often used.
        if (this.bypass == null) {
            v.bypass = Collections.singleton(n);
        }
        else {
            v.bypass = new ArrayList(this.bypass.size()+1);
            v.bypass.addAll(bypass);
            v.bypass.add(n);
        }

        return v;
    }

    /** Return a new visitor that will bypass all nodes in collection c. */
    public HaltingVisitor bypass(Collection c) {
        if (c == null) return this;

        HaltingVisitor v = (HaltingVisitor) copy();

        // FIXME: Using a collection is expensive, but is hopefully not
        // often used.
        if (this.bypass == null) {
            v.bypass = new ArrayList(c);
        }
        else {
            v.bypass = new ArrayList(this.bypass.size()+c.size());
            v.bypass.addAll(bypass);
            v.bypass.addAll(c);
        }

        return v;
    }

    public Node override(Node parent, Node n) {
        if (bypassParent != null && bypassParent == parent) {
            // System.out.println("bypassing " + n +
            //                    " (child of " + parent + ")");
            return n;
        }

        if (bypass != null) {
            for (Iterator i = bypass.iterator(); i.hasNext(); ) {
                if (i.next() == n) {
                    // System.out.println("bypassing " + n);
                    return n;
                }
            }
        }

        return null;
    }
}
