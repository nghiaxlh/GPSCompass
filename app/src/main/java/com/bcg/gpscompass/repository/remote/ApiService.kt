package com.bcg.gpscompass.repository.remote

import com.bcg.gpscompass.BuildConfig
import com.bcg.gpscompass.repository.model.Geocoding
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(ApiUtil.GEOCODING + "{lat},{lon}.json")
    suspend fun getDataList(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double,
        @Query("access_token") token: String = BuildConfig.MapboxAccessToken
    ): Geocoding

}