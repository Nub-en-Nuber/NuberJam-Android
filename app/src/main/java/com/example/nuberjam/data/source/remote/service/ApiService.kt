package com.example.nuberjam.data.source.remote.service

import com.example.nuberjam.data.source.remote.response.DataResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
}