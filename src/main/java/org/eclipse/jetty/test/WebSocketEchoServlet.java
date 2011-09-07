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
        getServletContext().getNamedDispatcher("default").forward(req,resp);
    }

    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol)
    {
        LOG.info("doWebSocketConnect(" + request + ", \"" + protocol + "\")");
        return new EchoWebSocket();
    }

    private static class EchoWebSocket implements WebSocket, WebSocket.OnTextMessage
    {
        private static final Logger LOG = LoggerFactory.getLogger(WebSocketEchoServlet.class);
        private static final int KBYTE = 1024;
        private static final int MBYTE = KBYTE * KBYTE;
        private Connection conn;

        public void onOpen(Connection connection)
        {
            LOG.info("onOpen(" + connection + ")");
            this.conn = connection;
            this.conn.setMaxTextMessageSize(MBYTE * 2);
            this.conn.setMaxBinaryMessageSize(MBYTE * 2);
        }

        public void onClose(int closeCode, String message)
        {
            LOG.info("onClose(" + closeCode + ", \"" + message + "\")");
        }

        public void onMessage(String data)
        {
            try
            {
                conn.sendMessage(data);
            }
            catch (IOException e)
            {
                LOG.error("Unable to send message",e);
            }
        }
    }
}
