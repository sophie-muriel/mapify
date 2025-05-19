package com.mapify.model

data class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var city: String = "",
    var country: String = ""
) {
    override fun toString(): String {
        return "$city, $country ($latitude, $longitude)"
    }
}