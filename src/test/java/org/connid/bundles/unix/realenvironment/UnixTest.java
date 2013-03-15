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

import org.connid.bundles.unix.UnixConfiguration;
import org.connid.bundles.unix.UnixConnector;
import org.connid.bundles.unix.utilities.SharedTestMethods;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.junit.Test;

public class UnixTest extends SharedTestMethods {

    @Test
    public final void testConnection() {
        final UnixConnector connector = new UnixConnector();
        connector.init(createConfiguration());
        connector.test();
        connector.dispose();
    }

    @Test(expected = ConnectorException.class)
    public final void testWrongConnection() {
        final UnixConnector connector = new UnixConnector();
        UnixConfiguration unixConfiguration = createConfiguration();
        unixConfiguration.setHostname("wrongaddresswrong");
        connector.init(unixConfiguration);
        connector.test();
        connector.dispose();
    }
}
