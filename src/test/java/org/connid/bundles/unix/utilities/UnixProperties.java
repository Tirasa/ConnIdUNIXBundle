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
