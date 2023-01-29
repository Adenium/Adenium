package io.adenium.utils;

import io.adenium.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringUtil {
    public static void write(String str, Charset charset, OutputStream outputStream) throws IOException {
        byte string[] = str.getBytes(charset);
        VarInt.writeCompactUInt32(string.length, false, outputStream);
        outputStream.write(string);
    }

    public static String read(Charset charset, InputStream inputStream) throws IOException {
        int length = VarInt.readCompactUInt32(false, inputStream);
        if (length <= 0) throw new IOException("unexpected string length '" + length + "'.");
        byte string[] = new byte[length];
        SerializableI.checkFullyRead(inputStream.read(string), length);
        return new String(string, charset);
    }
}
