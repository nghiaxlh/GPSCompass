package com.bcg.gpscompass.repository.model

import com.google.gson.annotations.SerializedName

data class Geocoding(
    @SerializedName("place_id") var placeId: Int? = null,
    @SerializedName("licence") var licence: String? = null,
    @SerializedName("powered_by") var poweredBy: String? = null,
    @SerializedName("osm_type") var osmType: String? = null,
    @SerializedName("osm_id") var osmId: Int? = null,
    @SerializedName("lat") var lat: String? = null,
    @SerializedName("lon") var lon: String? = null,
    @SerializedName("display_name") var displayName: String? = null,
    @SerializedName("address") var address: Address? = Address(),
    @SerializedName("boundingbox") var boundingbox: ArrayList<String> = arrayListOf()

)
data class Address(
    @SerializedName("building") var building: String? = null,
    @SerializedName("house_number") var houseNumber: String? = null,
    @SerializedName("road") var road: String? = null,
    @SerializedName("suburb") var suburb: String? = null,
    @SerializedName("city_district") var cityDistrict: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("postcode") var postcode: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("country_code") var countryCode: String? = null,
    @SerializedName("neighbourhood") var neighbourhood: String? = null
)
