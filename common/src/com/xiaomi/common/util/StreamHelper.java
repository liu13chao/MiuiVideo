package com.xiaomi.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class StreamHelper {

    static final String TAG = StreamHelper.class.getName();

    public static JSONObject toJSONObject(InputStream is) throws JSONException, IOException {
        return new JSONObject(toString(is));
    }

    public static String toString(InputStream is) throws IOException {
        final ByteArrayOutputStream baos = toByteArrayOutputStream(is);
        return (baos != null) ? baos.toString() : null;
    }

    public static byte[] toByteArray(InputStream is) throws IOException {
        final ByteArrayOutputStream baos = toByteArrayOutputStream(is);
        return (baos != null) ? baos.toByteArray() : null;
    }

    public static ByteArrayOutputStream toByteArrayOutputStream(InputStream is)
    throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buffer = new byte[512];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }

        return baos;
    }

    public static void wirteStringToStream(OutputStream out, String src) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write(src);
        writer.flush();
    }
}
