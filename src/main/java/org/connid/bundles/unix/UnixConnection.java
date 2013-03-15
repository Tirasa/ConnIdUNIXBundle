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
package org.connid.bundles.unix;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.connid.bundles.unix.utilities.Constants;
import org.connid.bundles.unix.utilities.DefaultProperties;
import org.connid.bundles.unix.utilities.Utilities;
import org.identityconnectors.common.logging.Log;

public class UnixConnection {

    private static final Log LOG = Log.getLog(UnixConnection.class);

    private static UnixConnection unixConnection = null;

    private static UnixConfiguration unixConfiguration = null;

    private static Session session;

    private ChannelExec execChannel;

    private InputStream fromServer;

    private static JSch jSch = new JSch();

    public static UnixConnection openConnection(
            final UnixConfiguration unixConfiguration) throws IOException, JSchException {
        if (unixConnection == null) {
            unixConnection = new UnixConnection(unixConfiguration);
        } else {
            unixConnection.setUnixConfuguration(unixConfiguration);
        }
        return unixConnection;
    }

    private UnixConnection(final UnixConfiguration unixConfiguration)
            throws IOException, JSchException {
        UnixConnection.unixConfiguration = unixConfiguration;
        initSession(unixConfiguration);
    }

    private void initSession(final UnixConfiguration unixConfiguration) throws JSchException {
        session = jSch.getSession(unixConfiguration.getAdmin(), unixConfiguration.getHostname(),
                unixConfiguration.getPort());
        session.setPassword(Utilities.getPlainPassword(unixConfiguration.getPassword()));
        session.setConfig(Constants.STRICT_HOST_KEY_CHECKING, "no");
    }

    private void setUnixConfuguration(final UnixConfiguration unixConfiguration) {
        UnixConnection.unixConfiguration = unixConfiguration;
    }

    public String execute(final String command) throws JSchException, IOException {
        if (!session.isConnected()) {
            initSession(unixConfiguration);
            session.connect(DefaultProperties.SSH_SOCKET_TIMEOUT);
        }
        if (execChannel == null || !execChannel.isConnected()) {
            execChannel = (ChannelExec) session.openChannel("exec");
            fromServer = execChannel.getInputStream();
        }
        LOG.info("Command to execute: " + command);
        execChannel.setCommand(command);
        execChannel.connect(DefaultProperties.SSH_SOCKET_TIMEOUT);
        return readOutput();
    }

    private String readOutput() throws IOException {
        String line;
        BufferedReader br = new BufferedReader(
                new InputStreamReader(fromServer));
        StringBuilder buffer = new StringBuilder();
        while ((line = br.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        if (execChannel.isClosed()) {
            LOG.info("exit-status: " + execChannel.getExitStatus());
        }
        sleep(1000);
        return buffer.toString();
    }

    private void sleep(final long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (Exception ee) {
            LOG.info("Failed to sleep between reads with pollTimeout: " + 1000, ee);
        }
    }

    public void testConnection() throws Exception {
        if (!session.isConnected()) {
            initSession(unixConfiguration);
        }
        session.connect(DefaultProperties.SSH_SOCKET_TIMEOUT);
        session.sendKeepAliveMsg();
    }

    public void authenticate(final String username, final String password) throws JSchException, IOException {
        session = jSch.getSession(username, unixConfiguration.getHostname(), unixConfiguration.getPort());
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(DefaultProperties.SSH_SOCKET_TIMEOUT);
        session.disconnect();
    }

    public void disconnect() {
        if (execChannel != null && execChannel.isConnected()) {
            execChannel.disconnect();
            LOG.info("Channel Shell is disconnected.");
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
            LOG.info("Session is disconnected.");
        }
    }
}
