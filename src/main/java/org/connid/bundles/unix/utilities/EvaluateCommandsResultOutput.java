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
package org.connid.bundles.unix.utilities;

import org.connid.bundles.unix.files.PasswdRow;
import org.connid.bundles.unix.files.PasswdRowElements;
import org.identityconnectors.common.StringUtil;

public class EvaluateCommandsResultOutput {

    public static boolean evaluateUserOrGroupExists(
            final String commandResult) {
        return !commandResult.isEmpty();
    }

    public static PasswdRow toPasswdRow(
            final String commandResult) {
        String[] userValues = commandResult.split(":");
        PasswdRow passwdRow = new PasswdRow();
        if (userValues.length == PasswdRowElements.values().length) {
            passwdRow.setUsername(userValues[
                    PasswdRowElements.USERNAME.getCode()]);
            passwdRow.setPasswordValidator(userValues[
                    PasswdRowElements.PASSWORD_VALIDATOR.getCode()]);
            passwdRow.setUserIdentifier(userValues[
                    PasswdRowElements.USER_IDENTIFIER.getCode()]);
            passwdRow.setGroupIdentifier(userValues[
                    PasswdRowElements.GROUP_IDENTIFIER.getCode()]);
            passwdRow.setComment(userValues[
                    PasswdRowElements.COMMENT.getCode()]);
            passwdRow.setHomeDirectory(userValues[
                    PasswdRowElements.HOME_DIRECTORY.getCode()]);
            passwdRow.setShell(userValues[
                    PasswdRowElements.SHELL.getCode()]);
        }
        return passwdRow;
    }

    public static boolean evaluateUserStatus(
            final String commandResult) {
        boolean userStatus = false;
        if (commandResult != null && StringUtil.isNotBlank(commandResult)
                && StringUtil.isNotEmpty(commandResult)) {
            String[] values = commandResult.split(":");
            userStatus = !values[1].startsWith("!");
        }
        return userStatus;
    }
}
