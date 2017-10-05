package org.eclipse.jetty.test;

import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.TypeUtil;

public class SHA1Util
{
    public static String toSha1(byte buf[])
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.digest(buf);
            return TypeUtil.toHexString(digest.digest());
        }
        catch (Throwable t)
        {
            return String.format("[error:%s:%s]",t.getClass().getSimpleName(),t.getMessage());
        }
    }
    
    public static String toSha1(String message)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            try (StringReader reader = new StringReader(message);
                    NoOpOutputStream noop = new NoOpOutputStream();
                    DigestOutputStream digester = new DigestOutputStream(noop,digest);
                    OutputStreamWriter writer = new OutputStreamWriter(digester))
            {
                IO.copy(reader,writer);
                writer.flush();
                return TypeUtil.toHexString(digest.digest());
            }
        }
        catch (Throwable t)
        {
            return String.format("[error:%s:%s]",t.getClass().getSimpleName(),t.getMessage());
        }
    }
}
