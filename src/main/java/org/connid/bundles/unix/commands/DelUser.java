/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Tirasa. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://IdentityConnectors.dev.java.net/legal/license.txt
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing the Covered Code, include this
 * CDDL Header Notice in each file
 * and include the License file at identityconnectors/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.connid.bundles.unix.commands;

import org.connid.bundles.unix.UnixConfiguration;

public class DelUser {

    private static final String USERDEL_COMMAND = "deluser";

    /**
     * Remove the home directory of the user and its mailspool. If --backup is specified, the files are deleted after
     * having per- formed the backup.
     */
    private static final String DELETE_USER_DIR_OPTION = "--remove-home";
    
    private UnixConfiguration unixConfiguration = null;
    private String username = "";

    public DelUser(final UnixConfiguration configuration,
            final String username) {
        unixConfiguration = configuration;
        this.username = username;
    }
    
    private String createUserDelCommand() {
        StringBuilder userdelCommand = new StringBuilder(USERDEL_COMMAND);
        userdelCommand.append(" ");
        if (unixConfiguration.isDeleteHomeDirectory()) {
            userdelCommand.append(DELETE_USER_DIR_OPTION).append(" ");
        }
        userdelCommand.append(username);
        return userdelCommand.toString();
    }
    
    public String deluser() {
        return createUserDelCommand();
    }

}
