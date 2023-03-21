package com.bcg.gpscompass.ui.screen.compass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcg.gpscompass.repository.GpsCompassRepository
import com.bcg.gpscompass.repository.remote.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CompassViewModel(private val repository: GpsCompassRepository): ViewModel() {
    val geoCodings : MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    fun getFlowerList(lat: Double, lon: Double) = viewModelScope.launch {
        geoCodings.value = ApiState.Loading
        repository.getGeocodingFromLocation(lat, lon)
            .catch { e ->
                geoCodings.value = ApiState.Failure(e)
            }.collect { data ->
                geoCodings.value = ApiState.Success(data)
            }
    } // getFlower
}