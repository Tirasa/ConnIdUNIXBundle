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
package org.connid.bundles.unix.utilities;

import java.util.ResourceBundle;

public class DefaultProperties {

    public static String UNIX_SHELL;
    public static String UNIX_USER_HOMEDIRECTORY;
    public static int SSH_SOCKET_TIMEOUT;
    public static String COMMENT_ATTRIBUTE = "";
    public static String SHELL_ATTRIBUTE = "";
    public static String HOMEDIRECTORY_ATTRIBUTE = "";

    static {
        ResourceBundle defaultProperties = ResourceBundle.getBundle("default");

        UNIX_SHELL = defaultProperties.getString("unix.user.shell");
        SSH_SOCKET_TIMEOUT = Integer.valueOf(
                defaultProperties.getString("ssh.socket.timeout")).intValue();
        UNIX_USER_HOMEDIRECTORY =
                defaultProperties.getString("unix.user.homedirectory");
        COMMENT_ATTRIBUTE = defaultProperties.getString("comment.attribute");
        SHELL_ATTRIBUTE = defaultProperties.getString("shell.attribute");
        HOMEDIRECTORY_ATTRIBUTE = defaultProperties.getString("homedirectory.attribute");
    }
}
