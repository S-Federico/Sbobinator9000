package com.imotorini.sbobinator9000.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imotorini.sbobinator9000.models.TranscriptionResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

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
        if (obj == null) return null;
        return om.writeValueAsString(obj);
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> tClass) throws IOException {
        return om.readValue(jsonString, tClass);
    }

    public static String getRequestBodyAsString(Request request) {
        if (request == null || request.body() == null) return null;
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return null;
        }
    }

    public static TranscriptionResponse parseResponse(Response response) throws IOException {
        if (response == null || response.body() == null) return null;
        String responseBodyStr = null;
        TranscriptionResponse transcriptionResponse = null;
        responseBodyStr = response.body().string();
        transcriptionResponse = CustomAndroidUtils.jsonStringToObject(responseBodyStr, TranscriptionResponse.class);
        return transcriptionResponse;
    }
}
