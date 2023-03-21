package com.bcg.gpscompass.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bcg.gpscompass.repository.GpsCompassRepository
import com.bcg.gpscompass.ui.screen.compass.CompassViewModel

class AppViewModelFactory(private val repository: GpsCompassRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CompassViewModel::class.java)) {
            CompassViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}