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

public class General {

    public static String catPasswdFile() {
        return "cat /etc/passwd";
    }

    public static String searchUserIntoPasswdFile(final String username) {
        return "cat /etc/passwd | grep " + username;
    }

    public static String searchGroupIntoGroupFile(String groupname) {
        return "cat /etc/group | grep " + groupname;
    }

    public static String searchUserStatusIntoShadowFile(final String username) {
        return "cat /etc/shadow | grep " + username;
    }
}