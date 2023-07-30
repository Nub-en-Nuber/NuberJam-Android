package com.example.nuberjam.data.source.remote.service

import com.example.nuberjam.data.source.remote.response.DataResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("account/retrieve.php?token=${ApiConfig.TOKEN}")
    suspend fun readAccountWithUsername(
        @Query("accountUsername") accountUsername: String,
    ): DataResponse

    @GET("account/retrieve.php?token=${ApiConfig.TOKEN}")
    suspend fun readAccountWithEmail(
        @Query("accountEmail") accountEmail: String,
    ): DataResponse
}