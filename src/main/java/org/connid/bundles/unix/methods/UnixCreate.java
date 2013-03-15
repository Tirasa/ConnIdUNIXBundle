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
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;

public class UnixCreate {

    private static final Log LOG = Log.getLog(UnixCreate.class);

    private Set<Attribute> attrs = null;

    private UnixConnection unixConnection = null;

    private UnixConfiguration configuration = null;

    private ObjectClass objectClass = null;

    String comment = "";

    String shell = "";

    String homeDirectory = "";

    boolean status = false;

    public UnixCreate(final ObjectClass oc,
            final UnixConfiguration unixConfiguration,
            final Set<Attribute> attributes) throws IOException, JSchException {
        this.attrs = attributes;
        unixConnection = UnixConnection.openConnection(unixConfiguration);
        configuration = unixConfiguration;
        objectClass = oc;
    }

    public Uid create() {
        try {
            return doCreate();
        } catch (Exception e) {
            LOG.error(e, "error during creation");
            throw new ConnectorException(e);
        }
    }

    private Uid doCreate() throws IOException, InterruptedException, JSchException {

        if (!objectClass.equals(ObjectClass.ACCOUNT)
                && (!objectClass.equals(ObjectClass.GROUP))) {
            throw new IllegalStateException("Wrong object class");
        }

        final Name name = AttributeUtil.getNameFromAttributes(attrs);

        if (name == null || StringUtil.isBlank(name.getNameValue())) {
            throw new IllegalArgumentException(
                    "No Name attribute provided in the attributes");
        }

        String username = name.getNameValue();

        if (objectClass.equals(ObjectClass.ACCOUNT)) {
            if (EvaluateCommandsResultOutput.evaluateUserOrGroupExists(
                    unixConnection.execute(UnixConnector.getCommandGenerator().userExists(username)))) {
                throw new ConnectorException(
                        "User " + username + " already exists");
            }

            for (Attribute attr : attrs) {
                if (attr.is(OperationalAttributes.PASSWORD_NAME)) {
                    continue;
                } else if (attr.is(OperationalAttributes.ENABLE_NAME)) {
                    // manage enable/disable status
                    if (attr.getValue() != null && !attr.getValue().isEmpty()) {
                        status = Boolean.parseBoolean(
                                attr.getValue().get(0).toString());
                    }
                } else if (attr.is(configuration.getCommentAttribute())) {
                    comment = attr.getValue().get(0).toString();
                } else if (attr.is(configuration.getShellAttribute())) {
                    shell = (String) attr.getValue().get(0).toString();
                } else if (attr.is(configuration.getHomeDirectoryAttribute())) {
                    homeDirectory = (String) attr.getValue().get(0).toString();
                }
            }

            final String password = Utilities.getPlainPassword(
                    AttributeUtil.getPasswordValue(attrs));

            unixConnection.execute(UnixConnector.getCommandGenerator().
                    createUser(username, password, comment, shell, homeDirectory, status));
            if (!status) {
                unixConnection.execute(UnixConnector.getCommandGenerator().lockUser(username));
            }
        } else if (objectClass.equals(ObjectClass.GROUP)) {
            if (EvaluateCommandsResultOutput.evaluateUserOrGroupExists(
                    unixConnection.execute(UnixConnector.getCommandGenerator().groupExists(username)))) {
                throw new ConnectorException(
                        "Group " + username + " already exists");
            }
            unixConnection.execute(UnixConnector.getCommandGenerator().createGroup(username));
        }

        return new Uid(username);
    }
}
