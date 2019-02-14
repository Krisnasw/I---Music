package com.intivestudio.ryppmusic.data.remote;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Music {

    @SerializedName("id")
    private Integer id;
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("genre_id")
    private Integer genre_id;
    @SerializedName("music_title")
    private String music_title;
    @SerializedName("music_slug")
    private String music_slug;
    @SerializedName("music_author")
    private String music_author;
    @SerializedName("music_album")
    private String music_album;
    @SerializedName("files")
    private String files;
    @SerializedName("images")
    private String images;
    @SerializedName("hitted")
    private Integer hitted;
    @SerializedName("created_at")
    private Date created_at;

    public Music(Integer id, String uuid, Integer genre_id, String music_title, String music_slug, String music_author, String music_album, String files, String images, Integer hitted, Date created_at) {
        this.id = id;
        this.uuid = uuid;
        this.genre_id = genre_id;
        this.music_title = music_title;
        this.music_slug = music_slug;
        this.music_author = music_author;
        this.music_album = music_album;
        this.files = files;
        this.images = images;
        this.hitted = hitted;
        this.created_at = created_at;
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

    public Integer getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(Integer genre_id) {
        this.genre_id = genre_id;
    }

    public String getMusic_title() {
        return music_title;
    }

    public void setMusic_title(String music_title) {
        this.music_title = music_title;
    }

    public String getMusic_slug() {
        return music_slug;
    }

    public void setMusic_slug(String music_slug) {
        this.music_slug = music_slug;
    }

    public String getMusic_author() {
        return music_author;
    }

    public void setMusic_author(String music_author) {
        this.music_author = music_author;
    }

    public String getMusic_album() {
        return music_album;
    }

    public void setMusic_album(String music_album) {
        this.music_album = music_album;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Integer getHitted() {
        return hitted;
    }

    public void setHitted(Integer hitted) {
        this.hitted = hitted;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
