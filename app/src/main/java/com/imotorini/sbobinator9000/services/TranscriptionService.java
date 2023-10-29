package com.imotorini.sbobinator9000.services;

import android.util.Log;

import com.imotorini.sbobinator9000.models.TranscriptionRequest;
import com.imotorini.sbobinator9000.utils.Constants;
import com.imotorini.sbobinator9000.utils.CustomAndroidUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class TranscriptionService {
    private static final String TAG = TranscriptionService.class.getSimpleName();
    private final OkHttpClient client;
    private final String baseUrl;
    private static final MediaType MEDIA_TYPE_AUDIO = MediaType.parse("audio/mpeg3");

    public TranscriptionService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.MINUTES)
                .readTimeout(120, TimeUnit.MINUTES)
                .writeTimeout(120, TimeUnit.MINUTES)
                .build();
    }

    public Response transcribe(byte[] file, String format) {
        Request request = buildRequest(file, format);
        Log.d(TAG, "Making request with endpoint: " + request.url());
        Response response = null;
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
        transcribeAsync(file, null, onResponseCallback);
    }

    public void transcribeAsync(byte[] file, String audioFormat, Callback onResponseCallback) {
        Request request = buildRequest(file, audioFormat);
        Log.d(TAG, "Making request with endpoint: " + request.url());
        client.newCall(request).enqueue(onResponseCallback);
    }

    private Request buildRequest(byte[] file, String audioFormat) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("audio", "filename.mp3",
                        RequestBody.create(file, MEDIA_TYPE_AUDIO));

        // Aggiungere l'estensione del file se fornita
        if (audioFormat != null) {
            builder.addFormDataPart("format", audioFormat);
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(baseUrl + Constants.STT_PATH)
                .post(requestBody)
                .build();

        return request;
    }

}

