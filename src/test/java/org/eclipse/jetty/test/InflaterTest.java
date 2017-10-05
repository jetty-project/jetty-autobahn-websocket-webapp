package org.eclipse.jetty.test;

import static java.nio.file.StandardOpenOption.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.BatchMode;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.api.extensions.IncomingFrames;
import org.eclipse.jetty.websocket.common.OpCode;
import org.eclipse.jetty.websocket.common.Parser;
import org.eclipse.jetty.websocket.common.extensions.AbstractExtension;
import org.junit.Ignore;
import org.junit.Test;

public class InflaterTest
{
    private static final Logger LOG = Log.getLogger(InflaterTest.InflateHandler.class);

    public static class AnonRsv1Extension extends AbstractExtension
    {
        @Override
        public void incomingFrame(Frame frame)
        {
        }

        @Override
        public void outgoingFrame(Frame frame, WriteCallback callback, BatchMode batchMode)
        {
        }

        @Override
        public boolean isRsv1User()
        {
            return true;
        }
    }

    public static class InflateHandler implements IncomingFrames
    {
        private static final Logger LOG = Log.getLogger(InflaterTest.InflateHandler.class);
        protected static final ByteBuffer TAIL_BYTES = ByteBuffer.wrap(new byte[] { 0x00, 0x00, (byte)0xFF, (byte)0xFF });

        private Inflater inflater = new Inflater(true);
        private AtomicInteger count = new AtomicInteger(0);
        private boolean incomingCompressed = false;

        @Override
        public void incomingError(Throwable t)
        {
            LOG.warn("Parse Error",t);
        }

        @Override
        public void incomingFrame(Frame frame)
        {
            LOG.debug("Parsed Frame: {} (#{})",frame,count.incrementAndGet());

            if (frame.getType().isData())
                incomingCompressed = frame.isRsv1();

            if (OpCode.isControlFrame(frame.getOpCode()) || !incomingCompressed)
            {
                // skip
                return;
            }

            try
            {
                ByteBuffer payload = frame.getPayload();
                decompress(payload, false);
                if (frame.isFin())
                {
                    decompress(TAIL_BYTES.slice(), true);
                }
            }
            catch (DataFormatException e)
            {
                LOG.warn(e);
                System.exit(-1);
                throw new RuntimeException(e);
            }

            if (frame.isFin())
                incomingCompressed = false;
        }

        private void decompress(ByteBuffer buf, boolean finish) throws DataFormatException
        {
            LOG.debug("Decompressing buffer {}",BufferUtil.toDetailString(buf));
            if ((buf == null) || (!buf.hasRemaining()))
            {
                return;
            }
            byte[] output = new byte[1024];

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
                    if (inflater.finished() || inflater.needsDictionary())
                    {
                        LOG.debug("Decompress: finished? {}",toDetail(inflater));
                        // We are finished ?
                        break;
                    }
                    else if (inflater.needsInput())
                    {
                        if (!supplyInput(buf))
                        {
                            break;
                        }
                    }
                }
                else
                {
                    // do something with output
                    LOG.debug("Decompressed {} bytes: {}",read,toDetail(inflater));
                    LOG.debug("Decompressed String: {}",new String(output,StandardCharsets.UTF_8));
                }
            }
            
            LOG.debug("Decompress: exiting {}",toDetail(inflater));
            
            if (finish)
            {
                inflater.reset();
            }
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

    public static class PayloadSaver implements IncomingFrames
    {
        protected static final byte[] TAIL_BYTES = new byte[] { 0x00, 0x00, (byte)0xFF, (byte)0xFF };
        private final Path outputPath;
        private final SeekableByteChannel channel;

        public PayloadSaver(Path file) throws IOException
        {
            this.outputPath = file;
            this.channel = Files.newByteChannel(outputPath,CREATE,WRITE,SYNC);
        }

        @Override
        public void incomingError(Throwable t)
        {
        }

        @Override
        public void incomingFrame(Frame frame)
        {
            try
            {
                if (frame.hasPayload())
                    channel.write(frame.getPayload().slice());
                if (frame.isFin())
                    channel.write(ByteBuffer.wrap(TAIL_BYTES));
            }
            catch (IOException e)
            {
                LOG.warn(e);
            }
        }
    }

    @Test
    public void testInflateCapturedIncomingFrames() throws IOException, DataFormatException
    {
        File targetDir = MavenTestingUtils.getTargetDir();

        WebSocketPolicy policy = WebSocketPolicy.newClientPolicy();
        ByteBufferPool bufferPool = new MappedByteBufferPool(8192);
        Parser parser = new Parser(policy,bufferPool);
        parser.setIncomingFramesHandler(new InflateHandler());
        parser.configureFromExtensions(Collections.singletonList(new AnonRsv1Extension()));

        Path file = new File(targetDir,"frame-20150508-144434-incoming.dat").toPath();
        try (SeekableByteChannel channel = Files.newByteChannel(file,READ))
        {
            long endPos = channel.size();
            ByteBuffer buf = ByteBuffer.allocate(8196);
            int len = 0;
            while (channel.position() < endPos)
            {
                len = channel.read(buf);
                buf.flip();
                LOG.debug("Read {} bytes from input: {}",len,BufferUtil.toDetailString(buf));
                if (len == (-1))
                {
                    break;
                }
                parser.parse(buf);
                buf.flip();
            }
        }
    }

    @Test
    @Ignore
    public void testIncomingFrames_toGzipFile() throws IOException, DataFormatException
    {
        File targetDir = MavenTestingUtils.getTargetDir();

        WebSocketPolicy policy = WebSocketPolicy.newClientPolicy();
        ByteBufferPool bufferPool = new MappedByteBufferPool(8192);
        Parser parser = new Parser(policy,bufferPool);

        Path outputFile = new File(targetDir,"compressed.gz").toPath();
        parser.setIncomingFramesHandler(new PayloadSaver(outputFile));
        parser.configureFromExtensions(Collections.singletonList(new AnonRsv1Extension()));

        Path file = new File(targetDir,"frame-20150508-144434-incoming.dat").toPath();
        try (SeekableByteChannel channel = Files.newByteChannel(file,READ))
        {
            long endPos = channel.size();
            ByteBuffer buf = ByteBuffer.allocate(8196);
            int len = 0;
            while (channel.position() < endPos)
            {
                len = channel.read(buf);
                buf.flip();
                LOG.debug("Read {} bytes from input: {}",len,BufferUtil.toDetailString(buf));
                if (len == (-1))
                {
                    break;
                }
                parser.parse(buf);
                buf.flip();
            }
        }
    }
}
