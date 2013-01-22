/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Tirasa. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://IdentityConnectors.dev.java.net/legal/license.txt
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing the Covered Code, include this
 * CDDL Header Notice in each file
 * and include the License file at identityconnectors/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.connid.bundles.unix.methods;

import com.jcraft.jsch.JSchException;
import java.io.IOException;
import org.connid.bundles.unix.UnixConfiguration;
import org.connid.bundles.unix.UnixConnection;
import org.connid.bundles.unix.UnixConnector;
import org.connid.bundles.unix.sshmanagement.CommandGenerator;
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