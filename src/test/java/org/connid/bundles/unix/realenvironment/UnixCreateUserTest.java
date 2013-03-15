/**
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
 * Copyright 2011-2013 Tirasa. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License"). You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at https://oss.oracle.com/licenses/CDDL
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at https://oss.oracle.com/licenses/CDDL.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.connid.bundles.unix.realenvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.connid.bundles.unix.UnixConnector;
import org.connid.bundles.unix.search.Operand;
import org.connid.bundles.unix.search.Operator;
import org.connid.bundles.unix.utilities.AttributesTestValue;
import org.connid.bundles.unix.utilities.SharedTestMethods;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnixCreateUserTest extends SharedTestMethods {

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

    @Test
    public final void createExistsUser() {
        boolean userExists = false;
        newAccount = connector.create(ObjectClass.ACCOUNT,
                createSetOfAttributes(name, attrs.getPassword(), true), null);
        assertEquals(name.getNameValue(), newAccount.getUidValue());
        try {
            connector.create(ObjectClass.ACCOUNT,
                    createSetOfAttributes(name, attrs.getPassword(), true),
                    null);
        } catch (Exception e) {
            userExists = true;
        }
        assertTrue(userExists);
    }

    @Test(expected = ConnectorException.class)
    public final void createLockedUser() {
        newAccount = connector.create(ObjectClass.ACCOUNT,
                createSetOfAttributes(name, attrs.getPassword(), false), null);
        connector.authenticate(ObjectClass.ACCOUNT, attrs.getUsername(),
                attrs.getGuardedPassword(), null);
    }

    @Test
    public final void createUnLockedUser() {
        newAccount = connector.create(ObjectClass.ACCOUNT,
                createSetOfAttributes(name, attrs.getPassword(), true), null);
        assertEquals(name.getNameValue(), newAccount.getUidValue());
        final Set<ConnectorObject> actual = new HashSet<ConnectorObject>();
        connector.executeQuery(ObjectClass.ACCOUNT, new Operand(Operator.EQ, Uid.NAME, newAccount.getUidValue(), false),
                new ResultsHandler() {

                    @Override
                    public boolean handle(final ConnectorObject connObj) {
                        actual.add(connObj);
                        return true;
                    }
                }, null);
        for (ConnectorObject connObj : actual) {
            assertEquals(name.getNameValue(), connObj.getName().getNameValue());
        }
        connector.authenticate(ObjectClass.ACCOUNT, newAccount.getUidValue(),
                attrs.getGuardedPassword(), null);
    }

    @Test(expected = ConnectorException.class)
    public void createWithWrongObjectClass() {
        connector.create(attrs.getWrongObjectClass(),
                createSetOfAttributes(name, attrs.getPassword(), true), null);
    }

    @Test(expected = ConnectorException.class)
    public void createTestWithNull() {
        connector.create(attrs.getWrongObjectClass(), null, null);
    }

    @Test(expected = ConnectorException.class)
    public void createTestWithNameNull() {
        connector.create(attrs.getWrongObjectClass(),
                createSetOfAttributes(null, attrs.getPassword(), true), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTestWithPasswordNull() {
        connector.create(attrs.getWrongObjectClass(),
                createSetOfAttributes(name, null, true), null);
    }

    @Test(expected = ConnectorException.class)
    public void createTestWithAllNull() {
        connector.create(null, null, null);
    }

    @After
    public final void close() {
        if (newAccount != null) {
            connector.delete(ObjectClass.ACCOUNT, newAccount, null);
        }
        connector.dispose();
    }
}