// ========================================================================
// Copyright (c) 2006-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at 
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses. 
// ========================================================================

package org.eclipse.jetty.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class TestServer
{
    public static void main(String[] args) throws Exception
    {
        int port = 9001;

        if (args.length > 0)
        {
            port = Integer.parseInt(args[0]);
        }

        System.out.printf("Jetty %s Websocket Echo Server%n",Server.getVersion());

        // Start Server
        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);

        // Serve some hello world servlets
        context.addServlet(new ServletHolder(new WebSocketEchoServlet()),"/");

        try
        {
            System.out.printf("Starting on port %d ...%n", port);
            server.start();
            System.out.println("Ready.");
            server.join();
        }
        finally
        {
            System.out.println("Shutting Down ...");
        }
    }
}
