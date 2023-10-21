package com.imotorini.sbobinator9000.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomAndroidUtils {

    private static ObjectMapper om = new ObjectMapper();

    public static byte[] fileToBytes(Uri fileUri, Context context) throws IOException {
        if (fileUri == null || context == null) return null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(fileUri);
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        inputStream.close();
        return byteBuffer.toByteArray();
    }

    public static String objectToJsonString(Object obj) throws JsonProcessingException {
        return om.writeValueAsString(obj);
    }
}
