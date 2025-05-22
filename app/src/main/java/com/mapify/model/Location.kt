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

    companion object {
        fun stringToLocation(string: String): Location {
            try {
                val parts = string.split("(", ")")
                if (parts.size != 3) throw IllegalArgumentException("Invalid format")

                val cityCountryPart = parts[0].trim()
                val coordinatesPart = parts[1].trim()

                val cityCountry = cityCountryPart.split(",").map { it.trim() }
                val coordinates = coordinatesPart.split(",").map { it.trim().toDouble() }

                return Location(
                    latitude = coordinates[0],
                    longitude = coordinates[1],
                    city = cityCountry.getOrNull(0) ?: "",
                    country = cityCountry.getOrNull(1) ?: ""
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return Location()
            }
        }
    }
}