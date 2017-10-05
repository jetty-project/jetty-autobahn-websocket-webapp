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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class EchoSocketCreator implements WebSocketCreator
{
    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp)
    {
        // manually negotiate extensions
        List<ExtensionConfig> negotiated = new ArrayList<>();
        // adding frame debug
        negotiated.add(new ExtensionConfig("@frame-debug; output-dir=target"));
        for (ExtensionConfig config : req.getExtensions())
        {
            if (config.getName().equals("permessage-deflate"))
            {
                // what we are interested in here
                negotiated.add(config);
                continue;
            }
            // skip all others
        }

        resp.setExtensions(negotiated);
        return new EchoSocket();
    }
}
