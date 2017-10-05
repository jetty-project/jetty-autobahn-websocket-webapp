//
//  ========================================================================
//  Copyright (c) 1995-2015 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.test;

import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket(maxIdleTime = 5000, maxBinaryMessageSize = 20_000_000, maxTextMessageSize = 20_000_000)
public class EchoSocket
{
    private static Logger LOG = Log.getLogger(EchoSocket.class);

    private Session conn;

    @OnWebSocketMessage
    public void onBinary(byte buf[], int offset, int len)
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug("onBinary(byte[{}],{},{})",buf.length,offset,len);
        }

        // echo the message back.
        this.conn.getRemote().sendBytesByFuture(ByteBuffer.wrap(buf,offset,len));
    }

    @OnWebSocketConnect
    public void onOpen(Session conn)
    {
        this.conn = conn;
    }

    @OnWebSocketMessage
    public void onText(String message)
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug("onText({}) SHA1:{}",message == null ? "<null>" : "length=" + message.length(),SHA1Util.toSha1(message));
        }

        // echo the message back.
        this.conn.getRemote().sendStringByFuture(message);
    }
}
