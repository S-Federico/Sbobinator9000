package com.imotorini.sbobinator9000.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranscriptionRequest {
    @JsonProperty("audio_bytes")
    private byte[] audioBytes;

    @JsonProperty("audio_format")
    private String format;

    public byte[] getAudioBytes() {
        return audioBytes;
    }

    public void setAudioBytes(byte[] audioBytes) {
        this.audioBytes = audioBytes;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "TranscriptionRequest{" +
                "audioBytes=" + Arrays.toString(audioBytes) +
                ", format='" + format + '\'' +
                '}';
    }
}
