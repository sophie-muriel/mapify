package com.mapify.viewmodel

import androidx.lifecycle.ViewModel
import com.mapify.model.Location
import com.mapify.model.Role
import com.mapify.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsersViewModel : ViewModel() {

    private val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        _users.value = getUsers()
    }

    fun create(user: User) {
        _users.value += user
    }

    fun findById(userId: String): User? {
        return _users.value.find { it.id == userId }
    }

    fun delete(userId: String) {
        _users.value -= users.value.filter { it.id != userId }
    }

    fun login(email: String, password: String): User? {
        return _users.value.find { it.email == email && it.password == password }
    }

    private fun getUsers(): List<User> {
        return listOf(
            User(
                id = "1",
                fullName = "Admin",
                email = "root",
                password = "root",
                role = Role.ADMIN,
                registrationLocation = Location(
                    latitude = 43230.2, longitude = 753948.8, country = "Colombia", city = "Armenia"
                )
            ),
            User(
                id = "2",
                fullName = "Average User",
                email = "user",
                password = "user",
                role = Role.CLIENT,
                registrationLocation = Location(
                    latitude = 43230.1, longitude = 753948.7, country = "Colombia", city = "Armenia"
                )
            )
        )
    }
}