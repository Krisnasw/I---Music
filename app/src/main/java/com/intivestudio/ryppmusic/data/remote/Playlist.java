package com.intivestudio.ryppmusic.data.remote;

import com.google.gson.annotations.SerializedName;

public class Playlist {

    @SerializedName("id")
    private Integer id;
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("playlist_name")
    private String playlistName;
    @SerializedName("playlist_slug")
    private String playlistSlug;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("name")
    private String name;

    public Playlist(Integer id, String uuid, String user_id, String playlistName, String playlistSlug, String createdAt, String name) {
        this.id = id;
        this.uuid = uuid;
        this.user_id = user_id;
        this.playlistName = playlistName;
        this.playlistSlug = playlistSlug;
        this.createdAt = createdAt;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistSlug() {
        return playlistSlug;
    }

    public void setPlaylistSlug(String playlistSlug) {
        this.playlistSlug = playlistSlug;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
