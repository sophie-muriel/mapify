package com.mapify.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.mapify.model.Location
import com.mapify.model.Role
import com.mapify.model.User
import com.mapify.utils.SharedPreferencesUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class UsersViewModel : ViewModel() {

    private val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun loadCurrentUser(userId: String) {
        _currentUser.value = findById(userId)
    }

    init {
        _users.value = getUsers()
    }

    fun create(user: User) {
        _users.value += user
    }

    fun edit(updatedUser: User, userId: String) {
        _users.value = _users.value.map { user ->
            if (user.id == userId) updatedUser else user
        }
    }

    fun find(email: String): User? {
        return _users.value.find { it.email == email }
    }

    fun findById(userId: String): User? {
        return _users.value.find { it.id == userId }
    }

    fun delete(userId: String) {
        _users.value = _users.value.filter { it.id != userId }
    }

    fun login(email: String, password: String): User? {
        return _users.value.find { it.email == email && it.password == password }
    }

    private fun getUsers(): List<User> {
        return listOf(
            User(
                id = UUID.randomUUID().toString(),
                fullName = "Admin",
                email = "root",
                password = "root",
                role = Role.ADMIN,
                registrationLocation = Location(
                    latitude = 43230.2, longitude = 753948.8, country = "Colombia", city = "Armenia"
                )
            ),
            User(
                id = UUID.randomUUID().toString(),
                fullName = "Average User",
                email = "user",
                password = "user",
                role = Role.CLIENT,
                registrationLocation = Location(
                    latitude = 43230.1, longitude = 753948.7, country = "Colombia", city = "Armenia"
                )
            ),
            User(
                id = UUID.randomUUID().toString(),
                fullName = "Barry McCoquiner",
                email = "barry.mccoquiner@example.com",
                password = "pass1",
                role = Role.CLIENT,
                registrationLocation = Location(0.0, 0.0, "USA", "City"),
                profileImageUrl = null
            ),
            User(
                id = UUID.randomUUID().toString(),
                fullName = "John Smith",
                email = "john.smith@example.com",
                password = "pass2",
                role = Role.CLIENT,
                registrationLocation = Location(0.0, 0.0, "USA", "City"),
                profileImageUrl = null
            ),
            User(
                id = UUID.randomUUID().toString(),
                fullName = "Alice Johnson",
                email = "alice.johnson@example.com",
                password = "pass3",
                role = Role.CLIENT,
                registrationLocation = Location(0.0, 0.0, "USA", "City"),
                profileImageUrl = null
            ),
            User(
                id = UUID.randomUUID().toString(),
                fullName = "Mike Cox",
                email = "mike.cox@example.com",
                password = "pass4",
                role = Role.CLIENT,
                registrationLocation = Location(0.0, 0.0, "USA", "City"),
                profileImageUrl = null
            ),
            User(
                id = UUID.randomUUID().toString(),
                fullName = "Hugh Jass",
                email = "hugh.jass@example.com",
                password = "pass5",
                role = Role.CLIENT,
                registrationLocation = Location(0.0, 0.0, "USA", "City"),
                profileImageUrl = null
            )
        )
    }
}