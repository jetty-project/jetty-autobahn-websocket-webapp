package org.eclipse.jetty.test;

import java.io.IOException;

import org.eclipse.jetty.util.FutureCallback;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.WebSocketConnection;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class EchoSocket
{
    private static Logger LOG = Log.getLogger(EchoSocket.class);

    private WebSocketConnection conn;

    @OnWebSocketMessage
    public void onBinary(byte buf[], int offset, int len)
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug("onBinary(byte[{}],{},{})",buf.length,offset,len);
        }

        // echo the message back.
        try
        {
            this.conn.write(buf,offset,len);
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }

    @OnWebSocketConnect
    public void onOpen(WebSocketConnection conn)
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
        try
        {
            this.conn.write(message);
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }
}
