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
package org.connid.bundles.unix.realenvironment;

import org.connid.bundles.unix.UnixConnector;
import org.connid.bundles.unix.utilities.AttributesTestValue;
import org.connid.bundles.unix.utilities.SharedTestMethods;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnixUpdateGroupTest extends SharedTestMethods {

    private UnixConnector connector = null;
    private Name name = null;
    private Uid newAccount = null;
    private AttributesTestValue attrs = null;

    @Before
    public final void initTest() {
        attrs = new AttributesTestValue();
        connector = new UnixConnector();
        connector.init(createConfiguration());
        name = new Name(attrs.getUsername());
    }

    public final void updateGroup() {
        newAccount = connector.create(ObjectClass.GROUP,
                createSetOfAttributes(name, attrs.getPassword(), true), null);
        connector.update(ObjectClass.GROUP, new Uid(attrs.getNewGroupName()),
                createSetOfAttributes(name, attrs.getNewPassword(), true),
                null);
        connector.delete(ObjectClass.GROUP,
                new Uid(attrs.getNewGroupName()), null);
    }

    @Test(expected = ConnectorException.class)
    public final void updateNotExistsGroup() {
        connector.update(ObjectClass.GROUP, new Uid(attrs.getWrongGroupName()),
                createSetOfAttributes(name, attrs.getNewPassword(), true),
                null);
    }

    @Test(expected = ConnectorException.class)
    public void updateWithWrongObjectClass() {
        newAccount = connector.create(ObjectClass.GROUP,
                createSetOfAttributes(name, attrs.getPassword(), true), null);
        connector.update(attrs.getWrongObjectClass(), newAccount,
                createSetOfAttributes(name, attrs.getNewPassword(), true),
                null);
    }

    @Test(expected = ConnectorException.class)
    public void updateWithNullUid() {
        connector.update(ObjectClass.GROUP, null,
                createSetOfAttributes(name, attrs.getNewPassword(), true),
                null);
    }

    @Test(expected = ConnectorException.class)
    public void updateWithNullSet() {
        newAccount = connector.create(ObjectClass.GROUP,
                createSetOfAttributes(name, attrs.getPassword(), true), null);
        connector.update(ObjectClass.GROUP, newAccount, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateWithNullPwd() {
        newAccount = connector.create(ObjectClass.GROUP,
                createSetOfAttributes(name, attrs.getPassword(), true), null);
        connector.update(ObjectClass.GROUP, newAccount,
                createSetOfAttributes(name, null, true), null);
    }

    @Test(expected = ConnectorException.class)
    public void updateWithNullUsername() {
        newAccount = connector.create(ObjectClass.GROUP,
                createSetOfAttributes(name, attrs.getPassword(), true), null);
        connector.update(ObjectClass.GROUP, newAccount,
                createSetOfAttributes(null, attrs.getPassword(), true), null);
    }

    @After
    public final void close() {
        if (newAccount != null) {
            connector.delete(ObjectClass.GROUP, newAccount, null);
        }
        connector.dispose();
    }
}
