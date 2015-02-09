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
import java.util.Set;
import org.connid.bundles.unix.UnixConfiguration;
import org.connid.bundles.unix.UnixConnection;
import org.connid.bundles.unix.UnixConnector;
import org.connid.bundles.unix.utilities.EvaluateCommandsResultOutput;
import org.connid.bundles.unix.utilities.Utilities;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;

public class UnixUpdate {

    private static final Log LOG = Log.getLog(UnixUpdate.class);

    private Set<Attribute> attrs = null;

    private UnixConfiguration configuration = null;

    private UnixConnection unixConnection = null;

    private Uid uid = null;

    private String newUserName = "";

    private String password = "";

    private boolean status = true;

    private String comment = "";

    private String shell = "";

    private String homeDirectory = "";

    private ObjectClass objectClass = null;

    public UnixUpdate(final ObjectClass oc,
            final UnixConfiguration unixConfiguration,
            final Uid uid, final Set<Attribute> attrs) throws IOException, JSchException {
        this.configuration = unixConfiguration;
        this.uid = uid;
        this.attrs = attrs;
        unixConnection = UnixConnection.openConnection(configuration);
        objectClass = oc;
    }

    public Uid update() {
        try {
            return doUpdate();
        } catch (Exception e) {
            LOG.error(e, "error during update operation");
            throw new ConnectorException("Error during update", e);
        }
    }

    private Uid doUpdate() throws IOException, JSchException {

        if (uid == null || StringUtil.isBlank(uid.getUidValue())) {
            throw new IllegalArgumentException("No Uid attribute provided in the attributes");
        }

        LOG.info("Update user: " + uid.getUidValue());

        if (!objectClass.equals(ObjectClass.ACCOUNT) && (!objectClass.equals(ObjectClass.GROUP))) {
            throw new IllegalStateException("Wrong object class");
        }

        if (objectClass.equals(ObjectClass.ACCOUNT)) {
            if (!EvaluateCommandsResultOutput.evaluateUserOrGroupExists(
                    unixConnection.execute(UnixConnector.getCommandGenerator().userExists(uid.getUidValue())))) {
                throw new ConnectorException("User " + uid + " do not exists");
            }
            for (Attribute attr : attrs) {
                if (attr.is(Name.NAME) || attr.is(Uid.NAME)) {
                    newUserName = (String) attr.getValue().get(0);
                } else if (attr.is(OperationalAttributes.PASSWORD_NAME)) {
                    password = Utilities.getPlainPassword((GuardedString) attr.getValue().get(0));
                } else if (attr.is(OperationalAttributes.ENABLE_NAME)) {
                    status = Boolean.parseBoolean(attr.getValue().get(0).toString());
                } else if (attr.is(configuration.getCommentAttribute())) {
                    comment = attr.getValue().get(0).toString();
                } else if (attr.is(configuration.getShellAttribute())) {
                    shell = (String) attr.getValue().get(0).toString();
                } else if (attr.is(configuration.getHomeDirectoryAttribute())) {
                    homeDirectory = (String) attr.getValue().get(0).toString();
                }
            }
            unixConnection.execute(UnixConnector.getCommandGenerator().updateUser(uid.getUidValue(), newUserName,
                    password, status, comment, shell, homeDirectory));
//            unixConnection.execute("mv /home/" + uid.getUidValue() + " /home/" + newUserName);
            unixConnection.
                    execute(UnixConnector.getCommandGenerator().moveHomeDirectory(uid.getUidValue(), newUserName));
            if (!status) {
                unixConnection.execute(UnixConnector.getCommandGenerator().lockUser(uid.getUidValue()));
            } else {
                unixConnection.execute(UnixConnector.getCommandGenerator().unlockUser(uid.getUidValue()));
            }
            if (StringUtil.isNotBlank(newUserName)
                    && StringUtil.isNotEmpty(newUserName)) {
                unixConnection.execute(UnixConnector.getCommandGenerator().updateGroup(uid.getUidValue(), newUserName));
            }
        } else if (objectClass.equals(ObjectClass.GROUP)) {
            if (!EvaluateCommandsResultOutput.evaluateUserOrGroupExists(
                    unixConnection.execute(UnixConnector.getCommandGenerator().groupExists(newUserName)))) {
                throw new ConnectorException("Group do not exists");
            }
            unixConnection.execute(UnixConnector.getCommandGenerator().updateGroup(uid.getUidValue(), newUserName));
        }
        return uid;
    }
}
