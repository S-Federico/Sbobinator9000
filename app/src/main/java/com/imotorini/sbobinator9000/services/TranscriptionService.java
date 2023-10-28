package com.imotorini.sbobinator9000.services;

import android.util.Log;

import com.imotorini.sbobinator9000.models.TranscriptionRequest;
import com.imotorini.sbobinator9000.utils.Constants;
import com.imotorini.sbobinator9000.utils.CustomAndroidUtils;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
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
    private String baseUrl;
    private static final MediaType MEDIA_TYPE_AUDIO = MediaType.parse("audio/mpeg3");
    private final Executor executor = Executors.newSingleThreadExecutor();

    public TranscriptionService(String baseUrl) {
        this.client = new OkHttpClient();
        discoverService();
    }
    private void discoverService() {
        executor.execute(() -> {
            try {
                JmDNS jmdns = JmDNS.create();
                jmdns.addServiceListener("_http._tcp.local.", new ServiceListener() {
                    @Override
                    public void serviceAdded(ServiceEvent event) {
                        jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
                    }

                    @Override
                    public void serviceRemoved(ServiceEvent event) {}

                    @Override
                    public void serviceResolved(ServiceEvent event) {
                        String ip = event.getInfo().getInetAddresses()[0].getHostAddress();
                        int port = event.getInfo().getPort();
                        baseUrl = "http://" + ip + ":" + port;
                        Log.d(TAG, "Service resolved with address: " + baseUrl);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public void transcribeAsync(byte[] file, Callback onResponseCallback) {
        transcribeAsync(file, null, onResponseCallback);
    }

    public void transcribeAsync(byte[] file, String audioFormat, Callback onResponseCallback) {
        if (baseUrl == null) {
            Log.e(TAG, "Service has not been discovered yet.");
            onResponseCallback.onFailure(null, new IOException("Service not discovered."));
            return;
        }

        Request request = buildRequest(file, audioFormat);
        Log.d(TAG, "Making request with endpoint: " + request.url());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onResponseCallback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onResponseCallback.onResponse(call, response);
            }
        });
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

