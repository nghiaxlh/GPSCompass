package com.bcg.gpscompass.repository.remote

object ApiUtil {

    private external fun baseUrlFromJNI(boolean: Boolean): String

    const val BASE_URL = "https://api.mapbox.com/"

    private const val SEARCH_API = "geocoding/v5/"
    private const val END_POINTS = "mapbox.places/"

    const val GEOCODING = SEARCH_API + END_POINTS


}