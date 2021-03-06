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

import org.identityconnectors.common.StringUtil;

public class UserMod {

    /**
     * The usermod command modifies the system account files to reflect the changes that are specified on the command
     * line.
     *
     *
     */
    private static final String USERMOD_COMMAND = "usermod";

    /**
     * The name of the user will be changed from LOGIN to NEW_LOGIN. Nothing else is changed. In particular, the user's
     * home directory name should probably be changed manually to reflect the new login name.
     */
    private static final String CHANGE_LOGIN_OPTION = "-l";

    /**
     * Lock a user's password. This puts a '!' in front of the encrypted password, effectively disabling the password.
     */
    private static final String LOCK_USER_OPTION = "-L";

    /**
     * Unlock a user's password. This removes the '!' in front of the encrypted password.
     */
    private static final String UNLOCK_USER_OPTION = "-U";

    /**
     * The new value of the user's password file comment field. It is normally modified using the chfn(1) utility.
     */
    private static final String COMMENT_OPTION = "-c";

    /**
     * The name of the user's new login shell. Setting this field to blank causes the system to select the default login
     * shell.
     */
    private static final String SHELL_OPTION = "-s";

    /**
     * The user's new login directory. If the -m option is given, the contents of the current home directory will be
     * moved to the new home directory, which is created if it does not already exist.
     */
    private static final String HOMEDIRECTORY_OPTION = "-d";

    /**
     * Move the content of the user's home directory to the new location. This option is only valid in combination with
     * the -d (or --home) option.
     */
    private static final String MOVE_FILE_OPTION = "-m";

    /**
     * The encrypted password, as returned by crypt(3).
     *
     * Note: This option is not recommended because the password (or encrypted password) will be visible by users
     * listing the processes.
     *
     * The password will be written in the local /etc/passwd or /etc/shadow file. This might differ from the password
     * database configured in your PAM configuration.
     *
     * You should make sure the password respects the system's password policy.
     */
    private static final String PASSWORD_OPTION = "-p";

    public String userMod(final String actualUsername,
            final String newUsername, final String password, final String comment, final String shell,
            final String homeDirectory) {
        return createUserModCommand(actualUsername, newUsername, password, comment, shell,
                homeDirectory);
    }

    private String createUserModCommand(final String actualUsername,
            final String newUsername, final String password, final String comment, final String shell,
            final String homeDirectory) {
        StringBuilder usermodCommand = new StringBuilder(USERMOD_COMMAND + " ");
        if ((StringUtil.isNotBlank(password))
                && (StringUtil.isNotEmpty(password))) {
            usermodCommand.append(PASSWORD_OPTION).append(" $(perl -e 'print crypt(")
                    .append(password).append(", ").append(password).append(");') ");
        }
        if ((StringUtil.isNotBlank(newUsername))
                && (StringUtil.isNotEmpty(newUsername))) {
            usermodCommand.append(CHANGE_LOGIN_OPTION).append(
                    " ").append(newUsername).append(" ");
        }
        if ((StringUtil.isNotBlank(comment))
                && (StringUtil.isNotEmpty(comment))) {
            usermodCommand.append(COMMENT_OPTION).append(
                    " ").append(comment).append(" ");
        }
        if ((StringUtil.isNotBlank(shell))
                && (StringUtil.isNotEmpty(shell))) {
            usermodCommand.append(SHELL_OPTION).append(
                    " ").append(shell).append(" ");
        }
        if ((StringUtil.isNotBlank(homeDirectory))
                && (StringUtil.isNotEmpty(homeDirectory))) {
            usermodCommand.append(HOMEDIRECTORY_OPTION).append(
                    " ").append(homeDirectory).append(
                    MOVE_FILE_OPTION).append(" ");
        }
        usermodCommand.append(actualUsername);
        return usermodCommand.toString();
    }

    public String lockUser(final String username) {
        return USERMOD_COMMAND + " " + LOCK_USER_OPTION + " " + username;
    }

    public String unlockUser(final String username) {
        return USERMOD_COMMAND + " " + UNLOCK_USER_OPTION + " " + username;
    }
}
