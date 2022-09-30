package com.imotorini.sbobinator9000.services;

import com.imotorini.sbobinator9000.BuildConfig;
import com.imotorini.sbobinator9000.utils.Constants;

import java.io.File;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TranscriptionService {

    private final OkHttpClient client;
    private static final MediaType MEDIA_TYPE_PLAINTEXT = MediaType.parse("audio/mpeg3");

    public TranscriptionService(OkHttpClient client) {
        this.client = client;
    }

    public TranscriptionService() {
        this.client = new OkHttpClient();
    }

    public void transcribe(File file, Callback onResponseCallback) {

        Request request;
        request = new Request.Builder()
                .url(BuildConfig.STT_BASE_URL + Constants.STT_PATH)
                .post(RequestBody.create(file, MEDIA_TYPE_PLAINTEXT))
                .build();

        client.newCall(request).enqueue(onResponseCallback);
    }
}
