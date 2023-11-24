package com.imotorini.sbobinator9000.models;

import org.junit.Assert;
import org.junit.Test;

public class TranscriptionRequestTest {

    @Test
    public void testToString() {
        TranscriptionRequest transcriptionRequest = new TranscriptionRequest();
        transcriptionRequest.setFormat("A");
        transcriptionRequest.setAudioBytes(new byte[]{1});
        Assert.assertEquals("TranscriptionRequest{audioBytes=[1], format='A'}", transcriptionRequest.toString());
    }
}
