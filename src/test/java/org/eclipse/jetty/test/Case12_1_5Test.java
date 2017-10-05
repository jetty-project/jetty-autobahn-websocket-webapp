package org.eclipse.jetty.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.DataFormatException;

import org.eclipse.jetty.toolchain.test.IO;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.TypeUtil;
import org.junit.Test;

public class Case12_1_5Test
{
    private static final String payloadHex = "d497bf6ac3301087f742dfc1780eeee9675936dd4ac60eed1b18a551b1436a055b2994d077af6c5a6842b82ebd41082ff7c7ba0f0e3e74babdc9b22c7f3806bfb1ddf0fc113a3fdc51610acaefb3d3929d0b5411cfefc81cdcb8cebef77e8cf1fce9315f5dcbadf77e72d70ab6c7d186de0f3187b3c4e8de7c704bdfda6fe75e45441725073f86d77ebffcd97e0f7f58866fa935f17bb1936bd57c8add146ff969ff5c9d31214926b04c65924c25cba49364d22c53952453c5321931a6aa92a4322c552d45a5204a55b3548d18953692540d4385242d05d65248d252602d85242d05d65248d252602d85242d05d65290b394d2b524156729085a8a9424156729085aca5c8efcaf548ca548f231a520b782c4bfa7e20aca7195b5dc12c61dfc8b4b4cc19a1a512ec6c291eb0b";
    private static final String expectedInflatedSha1 = "0c128dcadfd58d297bdb0e65946c1837c6acf9af";

    @Test
    public void testInflate() throws DataFormatException, IOException
    {
        byte payloadBuf[] = TypeUtil.fromHexString(payloadHex);
        saveFile(payloadBuf,"payload-deflated.dat");
        
        System.out.println("Deflated payload from autobahn = " + SHA1Util.toSha1(payloadBuf));
        
        Inflation inflation = new Inflation();
        List<ByteBuffer> out = inflation.inflate(ByteBuffer.wrap(payloadBuf));
        assertThat("Produced output buffers",out.size(),greaterThanOrEqualTo(1));

        int bufferLen = 0;
        for (ByteBuffer o : out)
        {
            bufferLen += o.remaining();
        }
        
        byte outBuf[] = new byte[bufferLen];
        int offset = 0;
        for (ByteBuffer o : out)
        {
            int len = o.remaining();
            o.get(outBuf,offset,len);
            offset += len;
        }
        
        System.out.println("Inflated Byte buffer = " + SHA1Util.toSha1(outBuf));

        String inflated = new String(outBuf,StandardCharsets.UTF_8);
        saveFile(outBuf,"payload-inflated.dat");
        assertThat("Inflated string size",inflated.length(),is(4096));
        String actualSha1 = SHA1Util.toSha1(inflated);
        assertThat("SHA1",actualSha1.toUpperCase(),is(expectedInflatedSha1.toUpperCase()));
    }

    private void saveFile(byte[] buf, String filename) throws IOException
    {
        Path outputPath = MavenTestingUtils.getTargetPath(filename);
        try (ByteArrayInputStream in = new ByteArrayInputStream(buf); OutputStream out = Files.newOutputStream(outputPath,StandardOpenOption.CREATE))
        {
            IO.copy(in,out);
        }
    }
}
