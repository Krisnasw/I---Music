package com.intivestudio.ryppmusic.data.services;

import com.intivestudio.ryppmusic.data.remote.ApiDefault;
import com.intivestudio.ryppmusic.data.remote.ApiLogin;
import com.intivestudio.ryppmusic.data.remote.ApiMusic;
import com.intivestudio.ryppmusic.data.remote.ApiPlaylist;
import com.intivestudio.ryppmusic.data.remote.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    Call<ApiLogin> doLogin(
        @Field("email") String email,
        @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register")
    Call<ApiDefault> doRegister(
        @Field("name") String name,
        @Field("email") String email,
        @Field("phone") String phone,
        @Field("gender") String gender,
        @Field("birthdate") String birthdate,
        @Field("password") String password
    );

    @GET("auth/user")
    Call<User> getUser(
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("user/update")
    Call<ApiDefault> doUpdateProfile(
        @Field("name") String name,
        @Field("phone") String phone,
        @Field("birthdate") String birthdate,
        @Header("Authorization") String token
    );

    @GET("music")
    Call<ApiMusic> getMusic(
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("playlist/create")
    Call<ApiDefault> addPlaylist(
              @Field("title") String title,
              @Header("Authorization") String token
    );

    @GET("playlists")
    Call<ApiPlaylist> getPlaylist(
        @Header("Authorization") String token
    );

    @GET("playlists/latest")
    Call<ApiPlaylist> getLatestPlaylist(
            @Header("Authorization") String token
    );

    @GET("music/{params}")
    Call<ApiMusic> getSearchMusic(
        @Path("params") String title,
        @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("music/playlist/add")
    Call<ApiDefault> addToPlaylist(
        @Header("Authorization") String token,
        @Field("music_id") String music_id,
        @Field("playlist_id") String playlist_id
    );

    @GET("playlists/detail/{id}")
    Call<ApiMusic> getDetailPlaylist(
        @Header("Authorization") String token,
        @Path("id") String id
    );

}
