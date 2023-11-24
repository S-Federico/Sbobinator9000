package com.imotorini.sbobinator9000;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imotorini.sbobinator9000.models.TranscriptionResponse;
import com.imotorini.sbobinator9000.services.TranscriptionService;
import com.imotorini.sbobinator9000.utils.CustomAndroidUtils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
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

        TranscriptionResponse transcriptionResponse = new TranscriptionResponse();
        transcriptionResponse.setStatus("OK");
        transcriptionResponse.setData("AAA");
        transcriptionResponse.setErrorMessage(null);

        server.setDispatcher(new Dispatcher() {
            @NonNull
            @Override
            public MockResponse dispatch(@NonNull RecordedRequest recordedRequest) throws InterruptedException {
                try {
                    return new MockResponse().setBody(CustomAndroidUtils.objectToJsonString(transcriptionResponse)).setResponseCode(200);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        TranscriptionService service = new TranscriptionService("http://127.0.0.1:9999");
        Response response = service.transcribe("RandomByteValue".getBytes());
        TranscriptionResponse transcriptionResponse1 = CustomAndroidUtils.parseResponse(response);
        assertEquals("AAA", transcriptionResponse1.getData());
        assertEquals("OK", transcriptionResponse1.getStatus());
        assertEquals(null, transcriptionResponse1.getErrorMessage());
    }
    @Test
    public void transcribeAsyncTest() throws InterruptedException {
        TranscriptionResponse transcriptionResponse = new TranscriptionResponse();
        transcriptionResponse.setStatus("OK");
        transcriptionResponse.setData("BBB");
        transcriptionResponse.setErrorMessage(null);

        server.setDispatcher(new Dispatcher() {
            @NonNull
            @Override
            public MockResponse dispatch(@NonNull RecordedRequest recordedRequest) throws InterruptedException {
                try {
                    return new MockResponse().setBody(CustomAndroidUtils.objectToJsonString(transcriptionResponse)).setResponseCode(200);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        TranscriptionService service = new TranscriptionService("http://127.0.0.1:9999");

        // Usiamo CountDownLatch per sincronizzare il test asincrono
        CountDownLatch latch = new CountDownLatch(1);

        service.transcribeAsync("RandomByteValue".getBytes(), new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                TranscriptionResponse transcriptionResponse1 = CustomAndroidUtils.parseResponse(response);
                assertEquals("BBB", transcriptionResponse1.getData());
                assertEquals("OK", transcriptionResponse1.getStatus());
                assertEquals(null, transcriptionResponse1.getErrorMessage());
                latch.countDown(); // decrementa il latch quando la risposta è ricevuta
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // gestire eventuali errori
                latch.countDown(); // decrementa il latch in caso di fallimento
            }
        });

        // Attendere al massimo 5 secondi per il completamento asincrono
        latch.await(5, TimeUnit.SECONDS);
    }
}