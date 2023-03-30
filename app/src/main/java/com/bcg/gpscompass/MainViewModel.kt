package com.bcg.gpscompass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcg.gpscompass.repository.firebase.RemoteConfigManager
import com.bcg.gpscompass.repository.model.firebase.UpdateAppModel
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.launch

class MainViewModel(private val remoteConfig: RemoteConfigManager?) : ViewModel() {
    init {
        viewModelScope.launch {
            remoteConfig?.fetchConfiguration()
        }
    }

    fun checkUpdateApp(): UpdateAppModel? {
        return remoteConfig?.checkUpdateApp()
    }
}