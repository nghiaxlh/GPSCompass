package com.bcg.gpscompass.repository.remote

object ApiUtil {

    private external fun baseUrlFromJNI(boolean: Boolean): String
    //https://geocode.maps.co/reverse?lat={latitude}&lon={longitude}
    const val BASE_GEOCODE_URL = "https://geocode.maps.co/"
    const val BASE_WEATHER_URL = "http://api.weatherapi.com/v1/"

    const val GEOCODING = "reverse"
    private const val FORECAST = "forecast.json"
    const val WEATHER_FORECAST = BASE_WEATHER_URL + FORECAST;
}