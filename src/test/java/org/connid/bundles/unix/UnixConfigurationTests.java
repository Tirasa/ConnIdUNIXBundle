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
package org.connid.bundles.unix;

import org.connid.bundles.unix.utilities.SharedTestMethods;
import org.connid.bundles.unix.utilities.UnixProperties;
import org.junit.Assert;
import org.junit.Test;

public class UnixConfigurationTests extends SharedTestMethods {

    /**
     * Tests setting and validating the parameters provided.
     */
    @Test
    public final void testValidate() {
        UnixConfiguration config = new UnixConfiguration();
        try {
            config.validate();
            Assert.fail();
        } catch (RuntimeException e) {
            // expected because configuration is incomplete
        }
        config = createConfiguration();
        config.validate();
        Assert.assertEquals(config.getHostname(),
                UnixProperties.UNIX_HOSTNAME);
    }
}
