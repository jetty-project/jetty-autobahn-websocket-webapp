package org.eclipse.jetty.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.server.WebSocketServlet;

@SuppressWarnings("serial")
public class WebSocketEchoServlet extends WebSocketServlet
{
    private static final int KBYTE = 1024;
    private static final int MBYTE = KBYTE * KBYTE;
    
    @Override
    public void registerWebSockets(WebSocketServerFactory factory)
    {
        factory.register(EchoSocket.class);
        factory.getPolicy().setMaxPayloadSize(10 * MBYTE);
        factory.getPolicy().setBufferSize(100 * KBYTE);
        factory.getPolicy().setMaxBinaryMessageSize(10 * MBYTE);
        factory.getPolicy().setMaxTextMessageSize(10 * MBYTE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.sendError(404);
    }
}
