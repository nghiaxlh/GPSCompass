package com.bcg.gpscompass.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bcg.gpscompass.MainViewModel
import com.bcg.gpscompass.repository.GpsCompassRepository
import com.bcg.gpscompass.repository.firebase.RemoteConfigManager
import com.bcg.gpscompass.ui.screen.compass.CompassViewModel
import com.bcg.gpscompass.ui.screen.weather.WeatherViewModel

class AppViewModelFactory(
    private val repository: GpsCompassRepository,
    private var remoteConfigManager: RemoteConfigManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CompassViewModel::class.java)) {
            CompassViewModel(this.repository) as T
        } else if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            WeatherViewModel(this.repository) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(this.remoteConfigManager) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}