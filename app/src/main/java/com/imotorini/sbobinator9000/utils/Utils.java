package com.imotorini.sbobinator9000.utils;

import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static byte[] fileToBytes(Uri fileUri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
