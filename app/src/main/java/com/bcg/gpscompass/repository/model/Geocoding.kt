package com.bcg.gpscompass.repository.model

import com.google.gson.annotations.SerializedName

data class Geocoding(
    val type: String,
    val query: List<Double>,
    val features: List<Feature>,
    val attribution: String,
)

data class Feature(
    val id: String,
    val type: String,
    @SerializedName("place_type")
    val placeType: List<String>,
    val relevance: Long,
    val properties: Properties,
    val text: String,
    @SerializedName("place_name")
    val placeName: String,
    val center: List<Double>,
    val geometry: Geometry,
    val address: String?,
    val context: List<Context>?,
    val bbox: List<Double>?,
)

data class Properties(
    val accuracy: String?,
    @SerializedName("mapbox_id")
    val mapboxId: String,
    val wikidata: String?,
    @SerializedName("short_code")
    val shortCode: String?,
)

data class Geometry(
    val type: String,
    val coordinates: List<Double>,
)

data class Context(
    val id: String,
    @SerializedName("mapbox_id")
    val mapboxId: String,
    val text: String,
    val wikidata: String?,
    @SerializedName("short_code")
    val shortCode: String?,
)
