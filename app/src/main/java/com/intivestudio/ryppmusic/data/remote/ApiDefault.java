package com.intivestudio.ryppmusic.data.remote;

import com.google.gson.annotations.SerializedName;

public class ApiDefault {

    @SerializedName("status")
    private Boolean status;
    @SerializedName("message")
    private String message;

    public ApiDefault(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
