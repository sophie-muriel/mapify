package com.mapify.model

class User(
    var id: String = "",
    var fullName: String = "",
    var email: String = "",
    var password: String = "",
    var role: Role = Role.CLIENT,
    var location: Location? = null,
    var profileImageUrl: String? = null
)
