/**
 * Copyright (C) 2011 ConnId (connid-dev@googlegroups.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
