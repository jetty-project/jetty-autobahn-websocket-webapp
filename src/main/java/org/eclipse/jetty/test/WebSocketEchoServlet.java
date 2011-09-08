package org.eclipse.jetty.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class WebSocketEchoServlet extends WebSocketServlet
{
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketEchoServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.sendError(404);
    }

    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol)
    {
        LOG.info("doWebSocketConnect(" + request + ", \"" + protocol + "\")");
        return new EchoWebSocket();
    }

    private static class EchoWebSocket implements WebSocket, WebSocket.OnTextMessage, WebSocket.OnBinaryMessage
    {
        private static final Logger LOG = LoggerFactory.getLogger(WebSocketEchoServlet.class);
        private static final int KBYTE = 1024;
        private static final int MBYTE = KBYTE * KBYTE;
        private Connection conn;

        public void onOpen(Connection connection)
        {
            LOG.info("onOpen(" + connection + ")");
            this.conn = connection;
            this.conn.setMaxTextMessageSize(MBYTE * 10);
            this.conn.setMaxBinaryMessageSize(MBYTE * 10);
        }

        public void onClose(int closeCode, String message)
        {
            LOG.info("onClose(" + closeCode + ", \"" + message + "\")");
        }

        public void onMessage(String data)
        {
            try
            {
                LOG.info("onMessage "+data.length()+"chars");
                conn.sendMessage(data);
            }
            catch (IOException e)
            {
                LOG.error("Unable to send text message",e);
            }
        }

        public void onMessage(byte[] data, int offset, int length)
        {
            try
            {
                LOG.info("onMessage "+length+"bytes");
                conn.sendMessage(data,offset,length);
            }
            catch (IOException e)
            {
                LOG.error("Unable to send binary message",e);
            }
        }
    }
}
