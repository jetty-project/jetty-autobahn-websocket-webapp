package org.eclipse.jetty.test;

import java.nio.ByteBuffer;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
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
            LOG.debug("onText({})",message);
        }

        // echo the message back.
        this.conn.getRemote().sendStringByFuture(message);
    }
}
