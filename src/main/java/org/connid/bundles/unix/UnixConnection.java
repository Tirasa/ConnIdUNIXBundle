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
package org.connid.bundles.unix;

import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.util.List;
import org.connid.bundles.unix.sshmanagement.SSHClient;

public class UnixConnection {

    private static UnixConfiguration unixConfiguration = null;

    SSHClient sshc = null;

    private UnixConnection(final UnixConfiguration unixConfiguration)
            throws IOException {
        this.unixConfiguration = unixConfiguration;
        sshc = new SSHClient(unixConfiguration);
    }

    public static UnixConnection openConnection(
            final UnixConfiguration unixConfiguration) throws IOException {
        return new UnixConnection(unixConfiguration);
    }

    public static UnixConfiguration getConfiguration() {
        return unixConfiguration;
    }

    public String userExists(final String username) throws JSchException, IOException {
        return sshc.userExists(username);
    }

    public String searchUser(final String username) throws JSchException, IOException {
        return sshc.searchUser(username);
    }

    public List<String> searchAllUser() throws JSchException, IOException {
        return sshc.searchAllUser();
    }

    public String userStatus(final String username) throws JSchException, IOException {
        return sshc.userStatus(username);
    }

    public String groupExists(String groupname) throws JSchException, IOException {
        return sshc.groupExists(groupname);
    }

    public void testConnection() throws JSchException {
        sshc.getChannelExec();
    }

    public void createUser(final String uidstring,
            final String password, final String comment, final String shell,
            final String homeDirectory, final boolean status) throws JSchException, IOException {
        sshc.createUser(uidstring, password, comment, shell,
                homeDirectory, status);
    }

    public void createGroup(String groupName) throws JSchException, IOException {
        sshc.createGroup(groupName);
    }

    public void updateUser(final String actualUsername,
            final String username, final String password, final boolean status,
            final String comment, final String shell, final String homeDir) throws JSchException, IOException {
        sshc.updateUser(actualUsername, username, password, status, comment,
                shell, homeDir);
    }

    public void updateGroup(String actualGroupName, String newUserName) throws JSchException, IOException {
        sshc.updateGroup(actualGroupName, newUserName);
    }

    public void deleteUser(final String username) throws JSchException, IOException {
        sshc.deleteUser(username);
    }

    public void deleteGroup(String groupName) throws JSchException, IOException {
        sshc.deleteGroup(groupName);
    }

    public void authenticate(final String username, final String password) throws JSchException, IOException {
        sshc.authenticate(username, password);
    }
}
