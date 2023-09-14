package com.example.nuberjam.data.source.remote.service

import com.example.nuberjam.data.source.remote.response.DataResponse
import com.example.nuberjam.utils.Constant
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
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


    // Album API Endpoint Collection
    @GET("album/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readAllAlbum(): DataResponse


    // Music API Endpoint Collection
    @GET("music/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readAllMusic(
        @Query("accountId") accountId: String,
    ): DataResponse

    @GET("music/retrieve.php?token=${Constant.TOKEN}")
    suspend fun readDetailMusic(
        @Query("accountId") accountId: String,
        @Query("musicId") musicId: String
    ): DataResponse
}