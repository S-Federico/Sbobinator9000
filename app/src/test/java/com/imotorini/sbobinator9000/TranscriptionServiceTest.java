package com.imotorini.sbobinator9000;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.annotation.NonNull;

import com.imotorini.sbobinator9000.services.TranscriptionService;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;


public class TranscriptionServiceTest {
    private static MockWebServer server;
    @BeforeClass
    public static void setup() throws IOException {
        server = new MockWebServer();
        server.start(9999);
        server.url("http://127.0.0.1");
    }

    @AfterClass
    public static void tearDown() throws IOException {
        if (server != null)
            server.shutdown();
    }

    @Test
    public void constructorTest() {
        TranscriptionService service = new TranscriptionService(BuildConfig.STT_BASE_URL);
        assertNotNull(service);
    }

    @Test
    public void transcribeTest() throws IOException {

        server.setDispatcher(new Dispatcher() {
            @NonNull
            @Override
            public MockResponse dispatch(@NonNull RecordedRequest recordedRequest) throws InterruptedException {
                return new MockResponse().setBody("AAA").setResponseCode(200);
            }
        });
        TranscriptionService service = new TranscriptionService("http://127.0.0.1:9999");
        Response response = service.transcribe("RandomByteValue".getBytes());
        assertEquals("AAA", response.body().string());
    }
}