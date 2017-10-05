package org.eclipse.jetty.test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class Inflation
{
    private static final int BUFFER_SIZE = 1;

    private static final Logger LOG = Log.getLogger(Inflation.class);

    private static final ByteBuffer TAIL_BYTES = ByteBuffer.wrap(new byte[] { 0x00, 0x00, (byte)0xFF, (byte)0xFF });
    private Inflater inflater = new Inflater(true);

    public Inflation()
    {
        this.inflater = new Inflater(true);
    }

    public List<ByteBuffer> inflate(ByteBuffer... payloads) throws DataFormatException
    {
        this.inflater.reset();

        List<ByteBuffer> ret = new ArrayList<>();

        for (int i = 0; i < payloads.length; i++)
        {
            ByteBuffer payload = payloads[i];
            decompress(ret,payload);
        }
        decompress(ret,TAIL_BYTES.slice());

        return ret;
    }

    private void decompress(List<ByteBuffer> out, ByteBuffer buf) throws DataFormatException
    {
        LOG.debug("Decompressing buffer {}",BufferUtil.toDetailString(buf));
        if ((buf == null) || (!buf.hasRemaining()))
        {
            return;
        }
        byte[] output = new byte[BUFFER_SIZE];

        if (inflater.needsInput() && !supplyInput(buf))
        {
            LOG.debug("Needed input, but no buffer could supply input");
            return;
        }

        int read = 0;
        while ((read = inflater.inflate(output)) >= 0)
        {
            if (read == 0)
            {
                LOG.debug("Decompress: read 0 {}",toDetail(inflater));
                break;
            }
            else
            {
                // do something with output
                LOG.debug("Decompressed {} bytes: {}",read,toDetail(inflater));
                // Copy buffer
                byte inf[] = new byte[read];
                System.arraycopy(output,0,inf,0,read);
                out.add(ByteBuffer.wrap(inf));
            }
        }

        LOG.debug("Decompress: exiting {}",toDetail(inflater));
    }

    private boolean supplyInput(ByteBuffer buf)
    {
        if (buf.remaining() > 0)
        {
            byte input[] = BufferUtil.toArray(buf);
            inflater.setInput(input);
            // mark buffer consumed
            buf.position(buf.position() + input.length);
            LOG.debug("Supplied {} input bytes: {}",input.length,toDetail(inflater));
            return true;
        }
        else
        {
            LOG.debug("Nothing left to consume, return");
            return false;
        }
    }

    private String toDetail(Inflater inflater)
    {
        return String.format("Inflater[finished=%b,read=%d,written=%d,remaining=%d,in=%d,out=%d]",inflater.finished(),inflater.getBytesRead(),
                inflater.getBytesWritten(),inflater.getRemaining(),inflater.getTotalIn(),inflater.getTotalOut());
    }
}
