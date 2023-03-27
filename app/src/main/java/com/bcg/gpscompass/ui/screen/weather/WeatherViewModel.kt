package com.bcg.gpscompass.ui.screen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcg.gpscompass.repository.GpsCompassRepository
import com.bcg.gpscompass.repository.remote.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: GpsCompassRepository): ViewModel() {
    val weatherData : MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    fun getWeather(lat: Double, lon: Double) = viewModelScope.launch {
        weatherData.value = ApiState.Loading
        repository.getWeatherFromLocation(lat, lon)
            .catch { e ->
                weatherData.value = ApiState.Failure(e)
            }.collect { data ->
                weatherData.value = ApiState.Success(data)
            }
    } // getFlower
}