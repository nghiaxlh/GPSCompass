package com.bcg.gpscompass.repository.remote

import com.bcg.gpscompass.BuildConfig
import com.bcg.gpscompass.repository.model.Geocoding
import com.bcg.gpscompass.repository.model.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(ApiUtil.GEOCODING)
    suspend fun getGeoCodingData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): Geocoding
    @GET(ApiUtil.WEATHER_FORECAST)
    suspend fun getWeatherData(
        @Query("q") query: String,
        @Query("days") lon: Int = 7,
        @Query("key") key: String = BuildConfig.WEATHER_ACCESS_TOKEN,
    ): Weather
}