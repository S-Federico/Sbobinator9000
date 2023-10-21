package com.imotorini.sbobinator9000.utils;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imotorini.sbobinator9000.models.TranscriptionRequest;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CustomAndroidUtilsTest {

    @Test
    public void testNullParams() throws IOException {
        byte[] res = CustomAndroidUtils.fileToBytes(null, null);
        Assert.assertNull(res);
    }

    @Test
    public void testFileToBytesNullUri() throws IOException {
        Context ctx = mock(Context.class);
        ContentResolver contentResolver = mock(ContentResolver.class);
        InputStream is = new ByteArrayInputStream("TEST".getBytes());

        when(ctx.getContentResolver()).thenReturn(contentResolver);
        when(contentResolver.openInputStream(any(Uri.class))).thenReturn(is);

        byte[] res = CustomAndroidUtils.fileToBytes(null, ctx);
        Assert.assertNull(res);
    }

    @Test
    public void testFileToBytesFileNotExists() throws IOException {
        Context ctx = mock(Context.class);
        ContentResolver contentResolver = mock(ContentResolver.class);
        InputStream is = new ByteArrayInputStream("TEST".getBytes());

        when(ctx.getContentResolver()).thenReturn(contentResolver);
        when(contentResolver.openInputStream(any(Uri.class))).thenThrow(FileNotFoundException.class);

        Assert.assertThrows(FileNotFoundException.class, () -> CustomAndroidUtils.fileToBytes(mock(Uri.class), ctx));
    }

    @Test
    public void testFileToBytesFileEmptyFile() throws IOException {
        Context ctx = mock(Context.class);
        ContentResolver contentResolver = mock(ContentResolver.class);
        InputStream is = new ByteArrayInputStream(new byte[]{});

        when(ctx.getContentResolver()).thenReturn(contentResolver);
        when(contentResolver.openInputStream(any(Uri.class))).thenReturn(is);

        byte[] res = CustomAndroidUtils.fileToBytes(mock(Uri.class), ctx);
        String resStr = new String(res);
        Assert.assertEquals("", resStr);
    }

    @Test
    public void testFileToBytesFileNonEmptyFile() throws IOException {
        Context ctx = mock(Context.class);
        ContentResolver contentResolver = mock(ContentResolver.class);
        InputStream is = new ByteArrayInputStream("HELLO".getBytes());

        when(ctx.getContentResolver()).thenReturn(contentResolver);
        when(contentResolver.openInputStream(any(Uri.class))).thenReturn(is);

        byte[] res = CustomAndroidUtils.fileToBytes(mock(Uri.class), ctx);
        String resStr = new String(res);
        Assert.assertEquals("HELLO", resStr);
    }


    // Test method objectToJsonString
    @Test
    public void testObjectToJsonStringNull() throws JsonProcessingException {
        String res = CustomAndroidUtils.objectToJsonString(null);
        Assert.assertNull(res);
    }

    @Test
    public void testObjectToJsonStringEmptyTranscriptionRequest() throws IOException {
        String res = CustomAndroidUtils.objectToJsonString(new TranscriptionRequest());
        String expected = "{\"audio_bytes\":null,\"audio_format\":null}";
        Assert.assertEquals(expected, res);
    }

    @Test
    public void testObjectToJsonStringTranscriptionRequestWithValues() throws IOException {
        TranscriptionRequest tr = new TranscriptionRequest();
        tr.setFormat("wav");
        tr.setAudioBytes("CIAO".getBytes());
        String res = CustomAndroidUtils.objectToJsonString(tr);
        String expected = "{\"audio_bytes\":\"Q0lBTw==\",\"audio_format\":\"wav\"}";
        Assert.assertEquals(expected, res);
    }
}