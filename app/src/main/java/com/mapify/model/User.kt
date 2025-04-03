package com.mapify.model

import android.location.Address

class User(
    var id: String,
    var fullName: String,
    var email: String,
    var password: String,
    var role: Role,
    var registrationLocation: Location
) {
}