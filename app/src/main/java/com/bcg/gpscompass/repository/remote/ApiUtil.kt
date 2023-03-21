package com.bcg.gpscompass.repository.remote

object ApiUtil {

    private external fun baseUrlFromJNI(boolean: Boolean): String
    //https://geocode.maps.co/reverse?lat={latitude}&lon={longitude}
    const val BASE_URL = "https://geocode.maps.co/"

    const val GEOCODING = "reverse"
}