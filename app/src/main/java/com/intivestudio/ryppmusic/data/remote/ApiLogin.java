package com.intivestudio.ryppmusic.data.remote;

import com.google.gson.annotations.SerializedName;

public class ApiLogin {

    @SerializedName("status")
    private Boolean status;
    @SerializedName("message")
    private String message;
    @SerializedName("token")
    private String token;

    public ApiLogin(Boolean status, String message, String token) {
        this.status = status;
        this.message = message;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
