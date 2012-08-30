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

package polyglot.frontend.passes;

import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.TypeExists;
import polyglot.types.Named;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;

public class TypeExistsPass extends ClassFilePass {
    protected Scheduler scheduler;
    protected TypeExists goal;
    protected TypeSystem ts;

    public TypeExistsPass(Scheduler scheduler, TypeSystem ts, TypeExists goal) {
        super(goal);
        this.scheduler = scheduler;
        this.ts = ts;
        this.goal = goal;
    }

    @Override
    public boolean run() {
        String name = goal.typeName();
        try {
            // Try to resolve the type; this may throw a
            // MissingDependencyException on the job to load the file
            // containing the type.
            Named n = ts.systemResolver().find(name);
            if (n instanceof Type) {
                return true;
            }
        }
        catch (SemanticException e) {
            ErrorQueue eq = ts.extensionInfo().compiler().errorQueue();
            eq.enqueue(ErrorInfo.SEMANTIC_ERROR, e.getMessage(), e.position());
        }
        return false;
    }
}
