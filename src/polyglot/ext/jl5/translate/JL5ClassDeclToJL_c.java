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
 * monitored by USAF Rome Laboratory, ONR Grants N00014-01-1-0968 and
 * N00014-09-1-0652, NSF Grants CNS-0208642, CNS-0430161, CCF-0133302,
 * and CCF-1054172, AFRL Contract FA8650-10-C-7022, an Alfred P. Sloan 
 * Research Fellowship, and an Intel Research Ph.D. Fellowship.
 *
 * See README for contributors.
 ******************************************************************************/
package polyglot.ext.jl5.translate;

import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.ext.jl5.ast.JL5AnnotatedElementExt;
import polyglot.ext.jl5.ast.JL5ClassDeclExt;
import polyglot.translate.ExtensionRewriter;
import polyglot.translate.ext.ClassDeclToExt_c;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;

public class JL5ClassDeclToJL_c extends ClassDeclToExt_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public NodeVisitor toExtEnter(ExtensionRewriter rw)
            throws SemanticException {
        //Skip annotations and parameter nodes
        ClassDecl cd = (ClassDecl) node();
        rw =
                (ExtensionRewriter) rw.bypass(JL5AnnotatedElementExt.annotationElems(cd));
        rw = (ExtensionRewriter) rw.bypass(JL5ClassDeclExt.paramTypes(cd));
        return rw;
    }

    @Override
    public Node toExt(ExtensionRewriter rw) throws SemanticException {
        ClassDecl cd = (ClassDecl) node();
        return rw.to_nf().ClassDecl(cd.position(),
                                    cd.flags(),
                                    cd.id(),
                                    cd.superClass(),
                                    cd.interfaces(),
                                    cd.body());
    }

}
