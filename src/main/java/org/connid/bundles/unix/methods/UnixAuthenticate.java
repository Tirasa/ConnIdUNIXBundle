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
package org.connid.bundles.unix.methods;

import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.net.UnknownHostException;
import org.connid.bundles.unix.UnixConfiguration;
import org.connid.bundles.unix.UnixConnection;
import org.connid.bundles.unix.utilities.Utilities;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;

public class UnixAuthenticate {

    private static final Log LOG = Log.getLog(UnixAuthenticate.class);

    private UnixConnection unixConnection = null;

    private String username = "";

    private GuardedString password = null;

    private ObjectClass objectClass;

    public UnixAuthenticate(final ObjectClass oc,
            final UnixConfiguration unixConfiguration,
            final String username, final GuardedString password)
            throws IOException, JSchException {
        unixConnection = UnixConnection.openConnection(unixConfiguration);
        this.username = username;
        this.password = password;
        objectClass = oc;
    }

    public Uid authenticate() {
        try {
            return doAuthenticate();
        } catch (Exception e) {
            LOG.error(e, "error during authentication of user: " + username);
            throw new ConnectorException("error during authentication of user: " + username, e);
        }
    }

    private Uid doAuthenticate() throws UnknownHostException, IOException, JSchException {
        if (!objectClass.equals(ObjectClass.ACCOUNT)) {
            throw new IllegalStateException("Wrong object class");
        }
        unixConnection.authenticate(username,
                Utilities.getPlainPassword(password));
        return new Uid(username);
    }
}
