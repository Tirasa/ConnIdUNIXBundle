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

public class Passwd {

    /**
     * passwd - update user's authentication tokens.
     *
     */
    private static final String PASSWD_COMMAND = "passwd";
    /**
     * This option is used to indicate that passwd should read the new password
     * from standard input, which can be a pipe.
     */
    private static final String READ_PASSWORD_FROM_STDIN = "--stdin";
    /**
     * This option is used to lock the specified account and it is available to
     * root only. The locking is performed by rendering the encrypted password
     * into an invalid string (by prefixing the encrypted string with an !).
     */
    private static final String LOCK_ACCOUNT = "-l";
    /**
     * This is the reverse of the -l option - it will unlock the account
     * password by removing the ! prefix. This option is avail - able to root
     * only. By default passwd will refuse to create a passwordless account (it
     * will not unlock an account that has only "!" as a password). The force
     * option -f will override this protection.
     *
     */
    private static final String UNLOCK_ACCOUNT = "-u";

    private String createChangeUserPasswordCommand(final String username,
            final String password) {
        return "echo " + password + " | " + PASSWD_COMMAND + " " + username
                + " " + READ_PASSWORD_FROM_STDIN;
    }

    public String setPassword(final String username,
            final String password) {
        return createChangeUserPasswordCommand(username, password);
    }

    public String lockUser(final String username) {
        return PASSWD_COMMAND + " " + LOCK_ACCOUNT + " " + username;
    }

    public String unlockUser(final String username) {
        return PASSWD_COMMAND + " " + UNLOCK_ACCOUNT + " " + username;
    }
}
