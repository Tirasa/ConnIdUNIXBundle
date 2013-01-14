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
package org.connid.bundles.unix.sshmanagement;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.connid.bundles.unix.UnixConfiguration;
import org.connid.bundles.unix.commands.DelUser;
import org.connid.bundles.unix.commands.General;
import org.connid.bundles.unix.commands.GroupAdd;
import org.connid.bundles.unix.commands.GroupDel;
import org.connid.bundles.unix.commands.GroupMod;
import org.connid.bundles.unix.commands.Sudo;
import org.connid.bundles.unix.commands.UserAdd;
import org.connid.bundles.unix.commands.UserMod;
import org.connid.bundles.unix.utilities.DefaultProperties;
import org.connid.bundles.unix.utilities.Utilities;
import org.identityconnectors.common.logging.Log;

public class SSHClient {

    private static final Log LOG = Log.getLog(SSHClient.class);

    Session session = null;

    private UnixConfiguration unixConfiguration = null;

    private String username;

    private String password;

    private JSch sshClient = null;

    public SSHClient(final UnixConfiguration unixConfiguration) throws IOException {
        this.unixConfiguration = unixConfiguration;
        SshClientInit();
        this.username = unixConfiguration.getAdmin();
        this.password = Utilities.getPlainPassword(
                unixConfiguration.getPassword());
    }

    private void SshClientInit() throws IOException {
        sshClient = new JSch();
    }

    private InputStream exec(String command) throws JSchException, IOException {
        LOG.info("Executing command: " + command);
        Channel channel = getChannelExec();
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        InputStream in = channel.getInputStream();
        channel.connect();
        return in;
    }

    public final Channel getChannelExec() throws JSchException {

        session = sshClient.getSession(username, unixConfiguration.getHostname(), unixConfiguration.getPort());
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect(DefaultProperties.SSH_SOCKET_TIMEOUT);
        LOG.info("Connected to host " + unixConfiguration.getHostname());

        return session.openChannel("exec");
    }

    public final String userExists(final String username) throws JSchException, IOException {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(
                General.searchUserIntoPasswdFile(username));

        return getOutput(exec(commandToExecute.toString()));
    }

    public final String searchUser(final String username) throws JSchException, IOException {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(
                General.searchUserIntoPasswdFile(username));
        return getOutput(exec(commandToExecute.toString()));
    }

    public final List<String> searchAllUser() throws JSchException, IOException {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(
                General.catPasswdFile());
        return getPasswdFileOutput(exec(commandToExecute.toString()));
    }

    public String groupExists(final String groupname) throws JSchException, IOException {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(
                General.searchGroupIntoGroupFile(groupname));
        return getOutput(exec(commandToExecute.toString()));
    }

    public String userStatus(final String username) throws JSchException, IOException {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(
                General.searchUserStatusIntoShadowFile(username));
        return getOutput(exec(commandToExecute.toString()));
    }

    public final void createUser(final String username, final String password,
            final String comment, final String shell,
            final String homeDirectory, final boolean status) throws JSchException, IOException {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(
                createUserAddCommand(username, password, comment, shell,
                homeDirectory));
        exec(commandToExecute.toString());
        session.disconnect();
        if (!status) {
            lockUser(username);
        }
    }

    private String createUserAddCommand(final String username,
            final String password, final String comment, final String shell,
            final String homeDirectory) {
        UserAdd userAddCommand = new UserAdd(
                unixConfiguration, username, password, comment, shell,
                homeDirectory);
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(userAddCommand.useradd());
        return commandToExecute.toString();
    }

    private void lockUser(final String username) throws JSchException, IOException {
        UserMod userModCommand = new UserMod();
        exec(userModCommand.lockUser(username));
        session.disconnect();
    }

    public void createGroup(final String groupName)
            throws IOException, JSchException {

        GroupAdd groupAddCommand = new GroupAdd(groupName);
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(groupAddCommand.groupadd());
        exec(commandToExecute.toString());
        session.disconnect();
    }

    public final void updateUser(final String actualUsername,
            final String newUserName, final String password,
            final boolean status, final String comment, final String shell,
            final String homeDirectory) throws JSchException, IOException {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(
                createModCommand(actualUsername, newUserName, password, comment,
                shell, homeDirectory));
        exec(commandToExecute.toString());
        session.disconnect();

        if (status) {
            unlockUser(actualUsername);
        } else {
            lockUser(actualUsername);
        }
    }

    private void unlockUser(String username)
            throws IOException, JSchException {
        UserMod userModCommand = new UserMod();
        exec(userModCommand.unlockUser(username));
        session.disconnect();
    }

    private String createModCommand(final String actualUsername,
            final String newUserName, final String password,
            final String comment, final String shell,
            final String homeDirectory) {
        UserMod userModCommand =
                new UserMod();
        StringBuilder commandToExecute = new StringBuilder();
        commandToExecute.append(userModCommand.userMod(
                actualUsername, newUserName, password, comment, shell, homeDirectory));
        return commandToExecute.toString();
    }

    public void updateGroup(final String actualGroupName,
            final String newUserName) throws JSchException, IOException {
        GroupMod groupModCommand =
                new GroupMod(actualGroupName, newUserName);
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(groupModCommand.groupMod());
        exec(commandToExecute.toString());
        session.disconnect();
    }

    public final void deleteUser(final String username) throws JSchException, IOException {
        DelUser userDelCommand =
                new DelUser(unixConfiguration, username);
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(userDelCommand.deluser());
//        exec(commandToExecute.toString());
//        session.disconnect();
        exec("sed -i -e \'/" + username + "/d\' /etc/shadow && sed -i -e \'/" + username
                + "/d\' /etc/passwd && sed -i -e \'/" + username + "/d\' /etc/group && rm -rf " + unixConfiguration.
                getBaseHomeDirectory() + "/" + username);
        session.disconnect();
    }

    public void deleteGroup(final String groupName) throws JSchException, IOException {
        GroupDel groupDelCommand = new GroupDel(groupName);
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand =
                    new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(groupDelCommand.groupDel());
        exec(commandToExecute.toString());
        session.disconnect();
    }

    public final void authenticate(final String username, final String password) throws JSchException {
        Session authSession = sshClient.getSession(username, unixConfiguration.getHostname(), unixConfiguration.
                getPort());
        authSession.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        authSession.setConfig(config);
        authSession.connect(DefaultProperties.SSH_SOCKET_TIMEOUT);
        authSession.disconnect();
    }

    private String getOutput(final InputStream inputStream) throws IOException {
        String line;
        BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream));
        StringBuilder buffer = new StringBuilder();
        while ((line = br.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        session.disconnect();
        return buffer.toString();
    }

    private List<String> getPasswdFileOutput(final InputStream inputStream) throws IOException {
        String line;
        BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream));
        List<String> passwdRows = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            passwdRows.add(line);
        }
        session.disconnect();
        return passwdRows;
    }
}
