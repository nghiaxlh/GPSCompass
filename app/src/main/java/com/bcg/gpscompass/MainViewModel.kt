package com.bcg.gpscompass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcg.gpscompass.repository.firebase.RemoteConfigManager
import kotlinx.coroutines.launch

class MainViewModel(remoteConfig: RemoteConfigManager?) : ViewModel() {
    init {
        viewModelScope.launch {
            remoteConfig?.fetchConfiguration()
        }
    }
}