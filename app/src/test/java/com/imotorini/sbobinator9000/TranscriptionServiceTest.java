package com.imotorini.sbobinator9000;

import com.imotorini.sbobinator9000.services.TranscriptionService;

import static org.junit.Assert.*;

import androidx.annotation.NonNull;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;


public class TranscriptionServiceTest {
    @Test
    public void constructorTest() {
        TranscriptionService service = new TranscriptionService();
        assertNotNull(service);
        service = new TranscriptionService(new OkHttpClient());
        assertNotNull(service);
    }

    @Test
    public void transcribeTest() {
        TranscriptionService service = new TranscriptionService();
        File f = new File("");
        service.transcribe(f, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}