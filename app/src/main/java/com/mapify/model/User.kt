package com.mapify.model

class User(
    var id: String,
    var fullName: String,
    var email: String,
    var password: String,
    var role: Role,
    var registrationLocation: Location,
    var profileImageUrl: String?= null
) {
}