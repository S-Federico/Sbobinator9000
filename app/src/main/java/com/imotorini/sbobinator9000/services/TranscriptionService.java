package com.imotorini.sbobinator9000.services;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imotorini.sbobinator9000.BuildConfig;
import com.imotorini.sbobinator9000.models.TranscriptionRequest;
import com.imotorini.sbobinator9000.utils.Constants;
import com.imotorini.sbobinator9000.utils.CustomAndroidUtils;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TranscriptionService {

    private static final String TAG = TranscriptionService.class.getSimpleName();

    private final OkHttpClient client;
    private final String baseUrl;
    private static final MediaType MEDIA_TYPE_PLAINTEXT = MediaType.parse("audio/mpeg3");

    public TranscriptionService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient();
    }

    public Response transcribe(byte[] file, String format) {

        TranscriptionRequest transcriptionRequest = new TranscriptionRequest();
        transcriptionRequest.setAudioBytes(file);
        transcriptionRequest.setFormat(format);

        String jsonStr = null;
        try {
            jsonStr = CustomAndroidUtils.objectToJsonString(transcriptionRequest);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "Failed to parse object. Reason: " + e);
            return null;
        }

        Request request;
        request = new Request.Builder()
                .url(baseUrl + Constants.STT_PATH)
                .post(RequestBody.create(jsonStr.getBytes(), MEDIA_TYPE_PLAINTEXT))
                .build();

        // client.newCall(request).enqueue(onResponseCallback);
        Response response=null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response transcribe(byte[] file) {
        return transcribe(file, null);
    }

    public void transcribeAsync(byte[] file, Callback onResponseCallback) {

        Request request = new Request.Builder()
                .url(BuildConfig.STT_BASE_URL + Constants.STT_PATH)
                .post(RequestBody.create(file, MEDIA_TYPE_PLAINTEXT))
                .build();

         client.newCall(request).enqueue(onResponseCallback);
    }
}
