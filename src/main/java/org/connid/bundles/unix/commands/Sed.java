/**
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
 * Copyright 2011-2013 Tirasa. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License"). You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at https://oss.oracle.com/licenses/CDDL
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at https://oss.oracle.com/licenses/CDDL.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.connid.bundles.unix.commands;

public class Sed {

    private String username = "";

    /**
     * sed - stream editor for filtering and transforming text
     */
    private static final String SED_COMMAND = "sed";

    /**
     * add the script to the commands to be executed
     */
    private static final String SCRIPT_OPTION = "-e";

    /**
     * edit files in place (makes backup if extension supplied)
     */
    private static final String EDIT_FILE_OPTION = "-i";

    public Sed(final String username) {
        this.username = username;
    }

    private String createSedCommand(final String filename) {
        StringBuilder sedCommand = new StringBuilder(SED_COMMAND);
        sedCommand.append(" ").append(EDIT_FILE_OPTION).append(" ").append(SCRIPT_OPTION).append(" ").append("\'/")
                .append(username).append("/d\'").append(" ").append(filename);
        return sedCommand.toString();
    }

    public String sedCommand(final String filename) {
        return createSedCommand(filename);
    }
}
