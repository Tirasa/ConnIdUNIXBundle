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
package org.connid.bundles.unix.methods;

import com.jcraft.jsch.JSchException;
import java.io.IOException;
import org.connid.bundles.unix.UnixConfiguration;
import org.connid.bundles.unix.UnixConnection;
import org.connid.bundles.unix.UnixConnector;
import org.connid.bundles.unix.utilities.EvaluateCommandsResultOutput;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;

public class UnixDelete {

    private static final Log LOG = Log.getLog(UnixDelete.class);

    private UnixConfiguration configuration = null;

    private UnixConnection unixConnection = null;

    private Uid uid = null;

    private ObjectClass objectClass = null;

    public UnixDelete(final ObjectClass oc,
            final UnixConfiguration unixConfiguration,
            final Uid uid) throws IOException, JSchException {
        configuration = unixConfiguration;
        unixConnection = UnixConnection.openConnection(configuration);
        this.uid = uid;
        objectClass = oc;
    }

    public final void delete() {
        try {
            doDelete();
        } catch (Exception e) {
            LOG.error(e, "error during delete operation");
            throw new ConnectorException(e);
        }
    }

    private void doDelete() throws IOException, InterruptedException, JSchException {

        if (uid == null || StringUtil.isBlank(uid.getUidValue())) {
            throw new IllegalArgumentException(
                    "No Uid attribute provided in the attributes");
        }

        LOG.info("Delete user: " + uid.getUidValue());

        if (!objectClass.equals(ObjectClass.ACCOUNT)
                && (!objectClass.equals(ObjectClass.GROUP))) {
            throw new IllegalStateException("Wrong object class");
        }

        if (objectClass.equals(ObjectClass.ACCOUNT)) {
            if (!EvaluateCommandsResultOutput.evaluateUserOrGroupExists(unixConnection.execute(UnixConnector.
                    getCommandGenerator().userExists(uid.getUidValue())))) {
                LOG.error("User do not exists");
                throw new ConnectorException("User do not exists");
            }
            unixConnection.execute(UnixConnector.getCommandGenerator().deleteUser(uid.getUidValue()));
        } else if (objectClass.equals(ObjectClass.GROUP)) {
            if (!EvaluateCommandsResultOutput.evaluateUserOrGroupExists(
                    unixConnection.execute(UnixConnector.getCommandGenerator().groupExists(uid.getUidValue())))) {
                throw new ConnectorException("Group do not exists");
            }
            unixConnection.execute(UnixConnector.getCommandGenerator().deleteGroup(uid.getUidValue()));
        }
    }
}