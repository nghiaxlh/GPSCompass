package com.bcg.gpscompass.repository.firebase

import com.bcg.gpscompass.BuildConfig
import com.bcg.gpscompass.repository.model.firebase.UpdateAppModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class RemoteConfigManager {
    private val remoteConfig
        get() = Firebase.remoteConfig

    init {
        if (BuildConfig.DEBUG) {
            val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
            remoteConfig.setConfigSettingsAsync(configSettings)
        }

        //remoteConfig.setDefaultsAsync(AppConfig.remote_config_defaults)
    }

    suspend fun fetchConfiguration(): Boolean =
        remoteConfig.fetchAndActivate().await()

    fun checkUpdateApp(): UpdateAppModel {
        val data: String = remoteConfig[SHOW_UPDATE_APP].asString()
        return Gson().fromJson(data, UpdateAppModel::class.java)
    }

    companion object {
        private const val SHOW_UPDATE_APP = "update_app"
        private const val FETCH_CONFIG_TRACE = "fetchConfig"
    }
}