package com.bcg.gpscompass.repository

import com.bcg.gpscompass.repository.model.Feature
import com.bcg.gpscompass.repository.remote.ApiService
import com.bcg.gpscompass.repository.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GpsCompassRepositoryImpl() : GpsCompassRepository {
    override fun getGeocodingFromLocation(lat: Double, lon: Double): Flow<List<Feature>> {
        return flow {
            val geoCoding = RetrofitClient.retrofit.getDataList(lat, lon)
            emit(geoCoding.features)
        }.flowOn(Dispatchers.IO)
    }


}

interface GpsCompassRepository {
    fun getGeocodingFromLocation(lat: Double, lon: Double): Flow<List<Feature>>
}

