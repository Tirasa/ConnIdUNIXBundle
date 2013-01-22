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
package org.connid.bundles.unix;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    private OutputStream toServer;

    private static JSch jSch = new JSch();

    private UnixConnection(final UnixConfiguration unixConfiguration)
            throws IOException, JSchException {
        UnixConnection.unixConfiguration = unixConfiguration;
        initSession(unixConfiguration);
    }

    public static UnixConnection openConnection(
            final UnixConfiguration unixConfiguration) throws IOException, JSchException {
        if (unixConnection == null) {
            unixConnection = new UnixConnection(unixConfiguration);
        } else {
            unixConnection.setUnixConfuguration(unixConfiguration);
        }
        return unixConnection;
    }

    private void setUnixConfuguration(final UnixConfiguration unixConfiguration) {
        UnixConnection.unixConfiguration = unixConfiguration;
    }

    public String execute(final String command) throws JSchException, IOException {
        if (!session.isConnected()) {
            initSession(unixConfiguration);
            session.connect(DefaultProperties.SSH_SOCKET_TIMEOUT);
            LOG.info("User " + unixConfiguration.getAdmin() + " authenticated");
        } else {
            LOG.info("User " + unixConfiguration.getAdmin() + " already authenticated");
        }
        if (execChannel == null || !execChannel.isConnected()) {
            execChannel = (ChannelExec) session.openChannel("exec");
            LOG.info("ChannelShell is connected.");

            fromServer = execChannel.getInputStream();
            toServer = execChannel.getOutputStream();
        } else {
            LOG.info("Channel (shell) still open.");
        }
        execChannel.setCommand(command);
        execChannel.connect(DefaultProperties.SSH_SOCKET_TIMEOUT);
        return getOutput();
    }

    private void initSession(final UnixConfiguration unixConfiguration) throws JSchException {
        session = jSch.getSession(unixConfiguration.getAdmin(), unixConfiguration.getHostname(),
                unixConfiguration.getPort());
        session.setPassword(Utilities.getPlainPassword(unixConfiguration.getPassword()));
        session.setConfig("StrictHostKeyChecking", "no");
    }

    private String getOutput() throws IOException {
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
        try {
            Thread.sleep(1000);
        } catch (Exception ee) {
        }
        return buffer.toString();
    }

    private void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (Exception ee) {
            LOG.info("Failed to sleep between reads with pollTimeout: " + 20, ee);
        }
    }

    public void testConnection() throws Exception {
        System.out.println("HOST: " + unixConfiguration.getHostname());
        if (!session.isConnected()) {
            initSession(unixConfiguration);
        }
        session.connect(DefaultProperties.SSH_SOCKET_TIMEOUT);
        session.sendKeepAliveMsg();
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

    public void authenticate(final String username, final String password) throws JSchException, IOException {
        session = jSch.getSession(username, unixConfiguration.getHostname(), unixConfiguration.getPort());
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(DefaultProperties.SSH_SOCKET_TIMEOUT);
        session.disconnect();
    }
//    private void writeToServer(String command) throws IOException {
//        String commandWithEnter = command;
//        if (!command.endsWith("\r\n")) {
//            commandWithEnter += "\r\n";
//        }
//        toServer.write((commandWithEnter).getBytes("UTF-8"));
//        toServer.flush();
//    }
//
//    private String getOutput2() throws IOException {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//
//        String linePrompt = "\\" + username + ">"; // indicates console has new-line, stop reading.
//        long timeout = System.currentTimeMillis() + DefaultProperties.SSH_SOCKET_TIMEOUT;
//
//        while (System.currentTimeMillis() < timeout
//                && !Util.byte2str(bos.toByteArray()).contains(linePrompt)) {
//            while (fromServer.available() > 0) {
//                int count = fromServer.read(buffer, 0, 1024);
//                if (count >= 0) {
//                    bos.write(buffer, 0, count);
//                } else {
//                    break;
//                }
//            }
//            if (execChannel.isClosed()) {
//                break;
//            }
//            // Don't spin like crazy though the while loop
////            sleep(20);
//        }
//        String result = bos.toString("UTF-8");
//        LOG.info("read from server: " + result);
//        return result;
//    }
}
