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
package org.connid.bundles.unix;

import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.util.Set;
import org.connid.bundles.unix.methods.UnixAuthenticate;
import org.connid.bundles.unix.methods.UnixCreate;
import org.connid.bundles.unix.methods.UnixDelete;
import org.connid.bundles.unix.methods.UnixExecuteQuery;
import org.connid.bundles.unix.methods.UnixTest;
import org.connid.bundles.unix.methods.UnixUpdate;
import org.connid.bundles.unix.search.Operand;
import org.connid.bundles.unix.sshmanagement.CommandGenerator;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.*;

@ConnectorClass(configurationClass = UnixConfiguration.class,
displayNameKey = "unix.connector.display")
public class UnixConnector implements Connector, CreateOp, UpdateOp,
        DeleteOp, TestOp, SearchOp<Operand>, AuthenticateOp {

    private static final Log LOG = Log.getLog(UnixConnector.class);

    private UnixConfiguration unixConfiguration;

    private static CommandGenerator commandGenerator = null;

    @Override
    public final Configuration getConfiguration() {
        return unixConfiguration;
    }

    @Override
    public final void init(final Configuration configuration) {
        unixConfiguration = (UnixConfiguration) configuration;
        commandGenerator = new CommandGenerator(unixConfiguration);
    }

    public static CommandGenerator getCommandGenerator() {
        return commandGenerator;
    }

    @Override
    public final void dispose() {
        try {
            UnixConnection.openConnection(unixConfiguration).disconnect();
        } catch (IOException ex) {
            LOG.error("Error in connection process", ex);
        } catch (JSchException jse) {
            LOG.error("Error in connection process", jse);
        }
    }

    @Override
    public final void test() {
        LOG.info("Remote connection test");
        try {
            new UnixTest(unixConfiguration).test();
        } catch (IOException ex) {
            LOG.error("Error in connection process", ex);
        } catch (JSchException jse) {
            LOG.error("Error in connection process", jse);
        }
    }

    @Override
    public final Uid create(final ObjectClass oc, final Set<Attribute> set, final OperationOptions oo) {
        LOG.info("Create new user");
        Uid uidResult = null;
        try {
            uidResult = new UnixCreate(oc, unixConfiguration, set).create();
        } catch (IOException ex) {
            LOG.error("Error in connection process", ex);
        } catch (JSchException ex) {
            LOG.error("Error in connection process", ex);
        }
        return uidResult;
    }

    @Override
    public final void delete(final ObjectClass oc, final Uid uid, final OperationOptions oo) {
        try {
            new UnixDelete(oc, unixConfiguration, uid).delete();
        } catch (IOException ex) {
            LOG.error("Error in connection process", ex);
        } catch (JSchException ex) {
            LOG.error("Error in connection process", ex);
        }
    }

    @Override
    public final Uid authenticate(final ObjectClass oc, final String username, final GuardedString gs,
            final OperationOptions oo) {
        Uid uidResult = null;
        try {
            LOG.info("Authenticate user: " + username);
            uidResult = new UnixAuthenticate(oc, unixConfiguration, username, gs).authenticate();
        } catch (IOException ex) {
            LOG.error("Error in connection process", ex);
        } catch (JSchException ex) {
            LOG.error("Error in connection process", ex);
        }
        return uidResult;
    }

    @Override
    public final Uid update(final ObjectClass oc, final Uid uid, final Set<Attribute> set, final OperationOptions oo) {
        try {
            new UnixUpdate(oc, unixConfiguration, uid, set).update();
        } catch (IOException ex) {
            LOG.error("Error in connection process", ex);
        } catch (JSchException ex) {
            LOG.error("Error in connection process", ex);
        }
        return uid;
    }

    @Override
    public final void executeQuery(final ObjectClass oc, final Operand filter, final ResultsHandler rh,
            final OperationOptions oo) {
        LOG.info("Execute query");
        try {
            new UnixExecuteQuery(unixConfiguration, oc, filter, rh).executeQuery();
        } catch (IOException ex) {
            LOG.error("Error in connection process", ex);
        } catch (JSchException ex) {
            LOG.error("Error in connection process", ex);
        }
    }

    @Override
    public final FilterTranslator<Operand> createFilterTranslator(final ObjectClass oc, final OperationOptions oo) {
        if (oc == null || (!oc.equals(ObjectClass.ACCOUNT)) && (!oc.equals(ObjectClass.GROUP))) {
            throw new IllegalArgumentException("Invalid objectclass");
        }
        return new UnixFilterTranslator();
    }
}
