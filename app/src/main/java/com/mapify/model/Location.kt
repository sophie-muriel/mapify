package com.mapify.model

data class Location(
    var latitude: Double,
    var longitude: Double,
    var country: String,
    var city: String
) {
    override fun toString(): String {
        return "$city, $country - ($latitude, $longitude)"
    }
}