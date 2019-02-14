package com.intivestudio.ryppmusic.data.remote;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiPlaylist {

    @SerializedName("status")
    private Boolean status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<Playlist> data;

    public ApiPlaylist(Boolean status, String message, List<Playlist> data) {
        this.status = status;
        this.message = message;
        this.data = data;
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

    public List<Playlist> getData() {
        return data;
    }

    public void setData(List<Playlist> data) {
        this.data = data;
    }
}
