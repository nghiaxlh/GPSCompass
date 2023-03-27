package com.bcg.gpscompass.repository

import com.bcg.gpscompass.repository.model.Geocoding
import com.bcg.gpscompass.repository.model.Weather
import com.bcg.gpscompass.repository.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GpsCompassRepositoryImpl : GpsCompassRepository {
    override fun getGeocodingFromLocation(lat: Double, lon: Double): Flow<Geocoding> {
        return flow {
            val geoCoding = RetrofitClient.geoCodingClient.getGeoCodingData(lat, lon)
            emit(geoCoding)
        }.flowOn(Dispatchers.IO)
    }

    override fun getWeatherFromLocation(lat: Double, lon: Double): Flow<Weather> {
        return flow {
            val weather = RetrofitClient.weatherClient.getWeatherData("$lat,$lon")
            emit(weather)
        }.flowOn(Dispatchers.IO)
    }


}

interface GpsCompassRepository {
    fun getGeocodingFromLocation(lat: Double, lon: Double): Flow<Geocoding>
    fun getWeatherFromLocation(lat: Double, lon: Double): Flow<Weather>
}

