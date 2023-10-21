package com.imotorini.sbobinator9000.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranscriptionResponse {
    @JsonProperty("status")
    private String status;
    @JsonProperty("data")
    private String data;
    @JsonProperty("error-message")
    private String errorMessage;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "TranscriptionResponse{" +
                "status='" + status + '\'' +
                ", data='" + data + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
