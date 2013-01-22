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

import com.jcraft.jsch.JSchException;
import java.io.IOException;
import org.connid.bundles.unix.UnixConfiguration;
import org.connid.bundles.unix.commands.DelUser;
import org.connid.bundles.unix.commands.General;
import org.connid.bundles.unix.commands.GroupAdd;
import org.connid.bundles.unix.commands.GroupDel;
import org.connid.bundles.unix.commands.GroupMod;
import org.connid.bundles.unix.commands.Sed;
import org.connid.bundles.unix.commands.Sudo;
import org.connid.bundles.unix.commands.UserAdd;
import org.connid.bundles.unix.commands.UserMod;

public class CommandGenerator {

    private UnixConfiguration unixConfiguration = null;

    public CommandGenerator(final UnixConfiguration unixConfiguration) {
        this.unixConfiguration = unixConfiguration;
    }

    public String userExists(final String username) {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(General.searchUserIntoPasswdFile(username)).toString();

        return commandToExecute.toString();
    }

    public String searchAllUser() {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        return commandToExecute.append(General.catPasswdFile()).toString();
    }

    public String groupExists(final String groupname) {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(General.searchGroupIntoGroupFile(groupname));
        return commandToExecute.toString();
    }

    public String userStatus(final String username) {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        return commandToExecute.append(General.searchUserStatusIntoShadowFile(username)).toString();
    }

    public String createUser(final String username, final String password,
            final String comment, final String shell,
            final String homeDirectory, final boolean status) {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(createUserAddCommand(username, password, comment, shell, homeDirectory));
        return commandToExecute.toString();
    }

    public String deleteUser(final String username) {
        DelUser userDelCommand =
                new DelUser(unixConfiguration, username);
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(userDelCommand.deluser());
        Sed sedCommand = new Sed(username);
        return sedCommand.sedCommand("/etc/shadow") + " && " + sedCommand.sedCommand("/etc/passwd") + " && "
                + sedCommand.sedCommand("/etc/group") + " &&" + "rm -rf " + unixConfiguration.
                getBaseHomeDirectory() + "/" + username;
    }

    private String createUserAddCommand(final String username,
            final String password, final String comment, final String shell,
            final String homeDirectory) {
        UserAdd userAddCommand = new UserAdd(unixConfiguration, username, password, comment, shell, homeDirectory);
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(userAddCommand.useradd());
        return commandToExecute.toString();
    }

    public String lockUser(final String username) {
        UserMod userModCommand = new UserMod();
        return userModCommand.lockUser(username);
    }

    public String createGroup(final String groupName)
            throws IOException, JSchException {

        GroupAdd groupAddCommand = new GroupAdd(groupName);
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(groupAddCommand.groupadd());
        return commandToExecute.toString();
    }

    public String updateUser(final String actualUsername,
            final String newUserName, final String password,
            final boolean status, final String comment, final String shell,
            final String homeDirectory) {
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(createModCommand(actualUsername, newUserName, password, comment, shell, homeDirectory));
        return commandToExecute.toString();
    }

    public String unlockUser(String username)
            throws IOException, JSchException {
        UserMod userModCommand = new UserMod();
        return userModCommand.unlockUser(username);
    }

    private String createModCommand(final String actualUsername,
            final String newUserName, final String password,
            final String comment, final String shell,
            final String homeDirectory) {
        UserMod userModCommand = new UserMod();
        StringBuilder commandToExecute = new StringBuilder();
        commandToExecute.append(userModCommand.userMod(actualUsername, newUserName, password, comment, shell,
                homeDirectory));
        return commandToExecute.toString();
    }

    public String updateGroup(final String actualGroupName,
            final String newUserName) {
        GroupMod groupModCommand = new GroupMod(actualGroupName, newUserName);
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(groupModCommand.groupMod());
        return commandToExecute.toString();
    }

    public String deleteGroup(final String groupName) {
        GroupDel groupDelCommand = new GroupDel(groupName);
        StringBuilder commandToExecute = new StringBuilder();
        if (!unixConfiguration.isRoot()) {
            Sudo sudoCommand = new Sudo(unixConfiguration.getSudoPassword());
            commandToExecute.append(sudoCommand.sudo()).append("; ");
        }
        commandToExecute.append(groupDelCommand.groupDel());
        return commandToExecute.toString();
    }

    public String moveHomeDirectory(final String oldUsername, final String newUsername) {
        return "mv " + unixConfiguration.getBaseHomeDirectory() + "/" + oldUsername + " " + unixConfiguration.
                getBaseHomeDirectory() + "/" + newUsername;
    }
}
