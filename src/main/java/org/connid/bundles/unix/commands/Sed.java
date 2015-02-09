/**
 * Copyright (C) ${project.inceptionYear} ConnId (connid-dev@googlegroups.com)
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
