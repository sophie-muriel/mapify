package com.mapify.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapify.model.Location
import com.mapify.model.Role
import com.mapify.model.User
import com.mapify.utils.SharedPreferencesUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class UsersViewModel : ViewModel() {

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> get() = _user

    private val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        _users.value = getUsers()
    }

    fun loadUser(context: Context): User? {
        val userId = SharedPreferencesUtils.getPreference(context)["userId"]
        _user.value = userId?.let { findById(it) }
        return _user.value
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
            ),
            User(
                id = "3",
                fullName = "Barry McCoquiner",
                email = "barry.mccoquiner@example.com",
                password = "pass1",
                role = Role.CLIENT,
                registrationLocation = Location(0.0, 0.0, "USA", "City"),
                profileImageUrl = null
            ),
            User(
                id = "4",
                fullName = "John Smith",
                email = "john.smith@example.com",
                password = "pass2",
                role = Role.CLIENT,
                registrationLocation = Location(0.0, 0.0, "USA", "City"),
                profileImageUrl = null
            ),
            User(
                id = "5",
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
                id = "6",
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