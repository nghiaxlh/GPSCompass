package com.bcg.gpscompass.repository.remote

import com.bcg.gpscompass.BuildConfig
import com.bcg.gpscompass.repository.model.Geocoding
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(ApiUtil.GEOCODING)
    suspend fun getDataList(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): Geocoding

}