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
