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

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.ObjectClass;

public class AttributesTestValue extends SharedTestMethods {

    private static final String WRONG_USERNAME = "wronguser";
    private static final String PASSWORD = "password";
    private final GuardedString GUARDED_PASSWORD =
            new GuardedString(getPassword().toCharArray());
    private final GuardedString NEW_GUARDED_PASSWORD =
            new GuardedString(getNewPassword().toCharArray());
    private final GuardedString WRONG_GUARDED_PASSWORD =
            new GuardedString("wrongpassword".toCharArray());
    private static final String NEW_PASSWORD = "newpassword";
    private final ObjectClass WRONG_OBJECTCLASS =
            new ObjectClass("WRONG_OBJECTCLASS");
    private static final String WRONG_GROUPNAME = "wronggroup";

    public String getUsername() {
        return "createtest" + randomNumber();
    }
    
    public String getNewUsername() {
        return "createtest" + randomNumber();
    }

    public String getWrongUsername() {
        return WRONG_USERNAME;
    }

    public GuardedString getGuardedPassword() {
        return GUARDED_PASSWORD;
    }

    public GuardedString getNewGuardedPassword() {
        return NEW_GUARDED_PASSWORD;
    }

    public GuardedString getWrongGuardedPassword() {
        return WRONG_GUARDED_PASSWORD;
    }

    public String getPassword() {
        return PASSWORD;
    }

    public String getNewPassword() {
        return NEW_PASSWORD;
    }

    public ObjectClass getWrongObjectClass() {
        return WRONG_OBJECTCLASS;
    }

    public String getGroupName() {
        return "grouptest" + randomNumber();
    }

    public String getWrongGroupName() {
        return WRONG_GROUPNAME;
    }

    public String getNewGroupName() {
        return "grouptest" + randomNumber();
    }

    private int randomNumber() {
        return (int) (Math.random() * 100000);
    }
}
