package com.example.nuberjam.data.source.remote.service

import com.example.nuberjam.data.source.remote.response.DataResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("account/login.php?token=${ApiConfig.TOKEN}")
    suspend fun makeLoginWithUsername(
        @Field("accountUsername") accountUsername: String,
        @Field("accountPassword") accountPassword: String
    ): DataResponse

    @FormUrlEncoded
    @POST("account/login.php?token=${ApiConfig.TOKEN}")
    suspend fun makeLoginWithEmail(
        @Field("accountEmail") accountEmail: String,
        @Field("accountPassword") accountPassword: String
    ): DataResponse

    @GET("account/retrieve.php?token=${ApiConfig.TOKEN}")
    suspend fun readAccountWithUsername(
        @Query("accountUsername") accountUsername: String,
    ): DataResponse

    @GET("account/retrieve.php?token=${ApiConfig.TOKEN}")
    suspend fun readAccountWithEmail(
        @Query("accountEmail") accountEmail: String,
    ): DataResponse

    @FormUrlEncoded
    @POST("account/add.php?token=${ApiConfig.TOKEN}")
    suspend fun addAccount(
        @Field("accountName") accountName: String,
        @Field("accountUsername") accountUsername: String,
        @Field("accountEmail") accountEmail: String,
        @Field("accountPassword") accountPassword: String
    ): DataResponse
}