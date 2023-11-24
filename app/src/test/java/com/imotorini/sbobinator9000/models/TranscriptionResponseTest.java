package com.imotorini.sbobinator9000.models;

import org.junit.Assert;
import org.junit.Test;

public class TranscriptionResponseTest {

    @Test
    public void testToString() {
        TranscriptionResponse transcriptionRequest = new TranscriptionResponse();
        transcriptionRequest.setStatus("A");
        transcriptionRequest.setData("DATA");
        transcriptionRequest.setErrorMessage("ERROR");
        Assert.assertEquals("TranscriptionResponse{status='A', data='DATA', errorMessage='ERROR'}", transcriptionRequest.toString());
    }
}
