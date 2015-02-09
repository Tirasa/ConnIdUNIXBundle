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
