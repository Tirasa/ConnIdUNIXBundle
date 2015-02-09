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
import org.connid.bundles.unix.UnixConfiguration;
import org.connid.bundles.unix.UnixConnection;
import org.connid.bundles.unix.search.Operand;
import org.connid.bundles.unix.search.Search;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ResultsHandler;

public class UnixExecuteQuery {

    private static final Log LOG = Log.getLog(UnixExecuteQuery.class);

    private UnixConnection connection = null;

    private UnixConfiguration unixConfiguration = null;

    private Operand filter = null;

    private ResultsHandler handler = null;

    private ObjectClass objectClass = null;

    public UnixExecuteQuery(final UnixConfiguration configuration,
            final ObjectClass oc, final Operand filter,
            final ResultsHandler rh) throws IOException, JSchException {
        connection = UnixConnection.openConnection(configuration);
        unixConfiguration = configuration;
        this.filter = filter;
        handler = rh;
        objectClass = oc;
    }

    public final void executeQuery() {
        try {
            doExecuteQuery();
        } catch (Exception e) {
            LOG.error(e, "error during execute query operation");
            throw new ConnectorException(e.getCause());
        }
    }

    private void doExecuteQuery() throws IOException, InterruptedException, JSchException {

        if (!objectClass.equals(ObjectClass.ACCOUNT)
                && (!objectClass.equals(ObjectClass.GROUP))) {
            throw new IllegalStateException("Wrong object class");
        }

        if (filter == null) {
            throw new ConnectorException("Filter is null");
        }

        switch (filter.getOperator()) {
            case EQ:
                new Search(unixConfiguration, connection, handler, objectClass, filter).equalSearch();
                break;
            case SW:
                new Search(unixConfiguration, connection, handler,
                        objectClass, filter).startsWithSearch();
                break;
            case EW:
                new Search(unixConfiguration, connection, handler,
                        objectClass, filter).endsWithSearch();
                break;
            case C:
                new Search(unixConfiguration, connection, handler,
                        objectClass, filter).containsSearch();
                break;
            case OR:
                new Search(unixConfiguration, connection, handler,
                        objectClass, filter.getFirstOperand()).orSearch();
                break;
            case AND:
                new Search(unixConfiguration, connection, handler,
                        objectClass, filter).andSearch();
                break;
            default:
                throw new ConnectorException("Wrong Operator");
        }
    }
}
