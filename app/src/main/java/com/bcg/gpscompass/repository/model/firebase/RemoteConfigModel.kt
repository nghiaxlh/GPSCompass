package com.bcg.gpscompass.repository.model.firebase

import com.google.gson.annotations.SerializedName

data class UpdateAppModel(
    @SerializedName("update_app") var update: Boolean? = null,
    @SerializedName("force_app") var force: Boolean? = null
)
