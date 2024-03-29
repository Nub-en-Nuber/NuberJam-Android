package com.example.nuberjam.data.source.remote.service

import com.example.nuberjam.data.source.remote.response.DataResponse
import com.example.nuberjam.utils.Constant
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    // Account API Endpoint Collection
    @FormUrlEncoded
    @POST("account/login.php?token=${Constant.TOKEN}")
    suspend fun makeLoginWithUsername(
        @Field("accountUsername") accountUsername: String,
        @Field("accountPassword") accountPassword: String
    ): DataResponse

    @FormUrlEncoded
    @POST("account/login.php?token=${Constant.TOKEN}")
    suspend fun makeLoginWithEmail(
        @Field("accountEmail") accountEmail: String,
        @Field("accountPassword") accountPassword: String
    ): DataResponse

    @GET("account/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readAccountWithUsername(
        @Query("accountUsername") accountUsername: String,
    ): DataResponse

    @GET("account/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readAccountWithEmail(
        @Query("accountEmail") accountEmail: String,
    ): DataResponse

    @FormUrlEncoded
    @POST("account/add.php?token=${Constant.TOKEN}")
    suspend fun addAccount(
        @Field("accountName") accountName: String,
        @Field("accountUsername") accountUsername: String,
        @Field("accountEmail") accountEmail: String,
        @Field("accountPassword") accountPassword: String
    ): DataResponse

    @FormUrlEncoded
    @POST("music/artist/check.php?token=${Constant.TOKEN}")
    suspend fun checkAccountArtist(
        @Field("accountId") accountId: String,
    ): DataResponse

    @Multipart
    @POST("account/edit.php?token=${Constant.TOKEN}")
    suspend fun updateAccount(
        @Query("accountId") accountId: String,
        @Part("accountName") accountName: RequestBody? = null,
        @Part("accountPassword") accountPassword: RequestBody? = null,
        @Part accountPhoto: MultipartBody.Part? = null,
    ): DataResponse

    @POST("account/delete.php?token=${Constant.TOKEN}")
    suspend fun deleteAccount(
        @Query("accountId") accountId: String,
    ): DataResponse


    // Album API Endpoint Collection
    @GET("album/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readAllAlbum(): DataResponse

    @GET("album/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readDetailAlbum(
        @Query("albumId") albumId: String,
        @Query("accountId") accountId: String,
    ): DataResponse


    // Music API Endpoint Collection
    @GET("music/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readAllMusic(
        @Query("accountId") accountId: String,
    ): DataResponse

    @GET("music/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readDetailMusic(
        @Query("accountId") accountId: String, @Query("musicId") musicId: String
    ): DataResponse


    // Playlist API Endpoint Collection
    @GET("playlist/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readAllPlaylist(
        @Query("accountId") accountId: String,
        @Query("q") query: String,
    ): DataResponse

    @GET("playlist/detail/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readPlaylistDetail(
        @Query("accountId") accountId: Int,
        @Query("playlistId") playlistId: Int,
    ): DataResponse

    @FormUrlEncoded
    @POST("playlist/add.php?token=${Constant.TOKEN}")
    suspend fun addPlaylist(
        @Field("playlistName") playlistName: String, @Field("accountId") accountId: String
    ): DataResponse

    @Multipart
    @POST("playlist/edit.php?token=${Constant.TOKEN}")
    suspend fun updatePlaylist(
        @Query("playlistId") playlistId: String,
        @Part("accountId") accountId: RequestBody,
        @Part("playlistName") playlistName: RequestBody? = null,
        @Part playlistPhoto: MultipartBody.Part? = null,
    ): DataResponse

    @POST("playlist/delete.php?token=${Constant.TOKEN}")
    suspend fun deletePlaylist(
        @Query("playlistId") playlistId: Int
    ): DataResponse
    @FormUrlEncoded
    @POST("playlist/detail/add.php?token=${Constant.TOKEN}")
    suspend fun addMusicToPlaylist(
        @Field("playlistId") playlistId: Int,
        @Field("musicId") musicId: Int,
    ): DataResponse

    @FormUrlEncoded
    @POST("playlist/detail/check.php?token=${Constant.TOKEN}")
    suspend fun checkMusicIsExist(
        @Field("playlistId") playlistId: Int,
        @Field("musicId") musicId: Int,
    ): DataResponse


    @POST("playlist/detail/delete.php?token=${Constant.TOKEN}")
    suspend fun deleteMusicFromPlaylist(
        @Query("playlistDetailId") playlistDetailId: Int
    ): DataResponse

    // Favorite API Endpoint Collection
    @GET("favorite/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readAllFavorite(
        @Query("accountId") accountId: String,
    ): DataResponse

    @FormUrlEncoded
    @POST("favorite/add.php?token=${Constant.TOKEN}")
    suspend fun addFavorite(
        @Field("musicId") musicId: Int,
        @Field("accountId") accountId: Int,
    ): DataResponse

    @POST("favorite/delete.php?token=${Constant.TOKEN}")
    suspend fun deleteFavorite(
        @Query("musicId") musicId: Int,
        @Query("accountId") accountId: Int,
    ): DataResponse
}