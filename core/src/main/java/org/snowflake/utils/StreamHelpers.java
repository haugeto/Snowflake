package org.snowflake.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class StreamHelpers {

    public static void pipeToStream(Reader in, Writer out) throws IOException {
        char[] buffer = new char[1024];
        for (int charCount = 0; (charCount = in.read(buffer, 0, buffer.length)) != -1;) {
            out.write(buffer, 0, charCount);
        }
    }

    public static void pipeToStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        for (int byteCount = 0; (byteCount = in.read(buffer, 0, buffer.length)) != -1;) {
            out.write(buffer, 0, byteCount);
        }
    }

}
