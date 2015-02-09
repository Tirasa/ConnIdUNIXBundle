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

public class UnixProperties {

    public static String UNIX_ADMIN;
    public static String UNIX_PASSWORD;
    public static String UNIX_HOSTNAME;
    public static String UNIX_PORT;
    public static String UNIX_BASE_HOME_DIRECTORY;
    public static String UNIX_USER_SHELL;

    static {
        ResourceBundle unixProperties = ResourceBundle.getBundle("unix");
        UNIX_ADMIN = unixProperties.getString("unix.admin");
        UNIX_PASSWORD = unixProperties.getString("unix.password");
        UNIX_HOSTNAME = unixProperties.getString("unix.hostname");
        UNIX_PORT = unixProperties.getString("unix.port");
        UNIX_USER_SHELL = unixProperties.getString("unix.user.shell");
        UNIX_BASE_HOME_DIRECTORY =
                unixProperties.getString("unix.base.home.directory");
    }
}
