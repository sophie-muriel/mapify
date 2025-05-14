package com.mapify.model

import android.location.Location

class User(
    var id: String,
    var fullName: String,
    var email: String,
    var password: String,
    var role: Role,
    var location: Location,
    var profileImageUrl: String? = null
)
