package com.mapify.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapify.model.Location
import com.mapify.model.User
import com.mapify.utils.RequestResult
import com.mapify.utils.SharedPreferencesUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UsersViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> get() = _user

    private val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _registerResult = MutableStateFlow<RequestResult?>(null)
    val registerResult: StateFlow<RequestResult?> = _registerResult.asStateFlow()

    private val _foundUser = MutableStateFlow<User?>(null)
    val foundUser: StateFlow<User?> = _foundUser.asStateFlow()

    init {
        getUsers()
    }

    fun loadUser(context: Context) {
        val userId = SharedPreferencesUtils.getPreference(context)["userId"]
        if (userId != null) {
            viewModelScope.launch { findByIdFirebase(userId, true) }
        }
    }

    fun resetRegisterResult() {
        _registerResult.value = null
    }

    fun create(user: User) {
        viewModelScope.launch {
            _registerResult.value = RequestResult.Loading
            _registerResult.value = kotlin.runCatching { createFirebase(user) }
                .fold (
                    onSuccess = { RequestResult.Success("User created successfully") },
                    onFailure = { RequestResult.Failure(it.message?: "Error creating user") }
                )
        }
    }

    private suspend fun createFirebase(user: User) {
        val responseUser = auth.createUserWithEmailAndPassword(user.email, user.password).await()
        val userId = responseUser.user?.uid ?: ""

        val userCopy = User (
            id = userId,
            fullName = user.fullName,
            email = user.email,
            password = "",
            role = user.role,
            location = user.location,
            profileImageUrl = user.profileImageUrl,
        )

        val userMap = mapOf(
            "id" to userCopy.id,
            "fullName" to userCopy.fullName,
            "email" to userCopy.email,
            "password" to userCopy.password,
            "role" to userCopy.role.name,
            "location" to mapOf(
                "latitude" to userCopy.location?.latitude,
                "longitude" to userCopy.location?.longitude
            ),
            "profileImageUrl" to userCopy.profileImageUrl
        )

        db.collection("users")
            .document(userId)
            .set(userMap)
            .await()
    }

    fun update(user: User) {
        viewModelScope.launch {
            _registerResult.value = RequestResult.Loading
            _registerResult.value = kotlin.runCatching { updateFirebase(user) }
                .fold (
                    onSuccess = { RequestResult.Success("User updated successfully") },
                    onFailure = { RequestResult.Failure(it.message?: "Error updating user") }
                )
        }
    }

    private suspend fun updateFirebase(user: User) {
        val userMap = mapOf(
            "id" to user.id,
            "fullName" to user.fullName,
            "email" to user.email,
            "password" to user.password,
            "role" to user.role.name,
            "location" to mapOf(
                "latitude" to user.location?.latitude,
                "longitude" to user.location?.longitude
            ),
            "profileImageUrl" to user.profileImageUrl
        )
        db.collection("users")
            .document(user.id)
            .set(userMap)
            .await()
    }

    fun find(email: String) {
        viewModelScope.launch {
            val foundUser = findByEmailFirebase(email)
            _foundUser.value = foundUser
        }
    }

    private suspend fun findByEmailFirebase(email: String): User? {
        val query = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()

        return query.documents.firstOrNull()?.toObject(User::class.java)?.apply {
            id = query.documents.firstOrNull()?.id ?: ""
            location = query.documents.firstOrNull()?.getLocationFromFirebase() ?: Location()
        }
    }

    fun findById(userId: String, isCurrent: Boolean) {
        viewModelScope.launch {
            findByIdFirebase(userId, isCurrent)
        }
    }

    private suspend fun findByIdFirebase(userId: String, current: Boolean) {
        val query = db.collection("users")
            .document(userId)
            .get()
            .await()

        val user = query.toObject(User::class.java)?.apply {
            id = query.id
            location = query.getLocationFromFirebase()
        }

        if (current) _user.value = user else _foundUser.value = user
    }

    fun delete(userId: String) {
        viewModelScope.launch {
            _registerResult.value = RequestResult.Loading
            _registerResult.value = kotlin.runCatching { deleteFirebase(userId) }
                .fold (
                    onSuccess = { RequestResult.Success("User deleted successfully") },
                    onFailure = { RequestResult.Failure(it.message?: "Error deleting user") }
                )
        }
    }

    private suspend fun deleteFirebase(userId: String) {
        db.collection("users")
            .document(userId)
            .delete()
            .await()

        val user = auth.currentUser
        if (user != null && user.uid == userId) {
            user.delete().await()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = RequestResult.Loading
            val result = kotlin.runCatching {
                loginFirebase(email, password)
            }
            _registerResult.value = result.fold(
                onSuccess = { RequestResult.Success("Logged in") },
                onFailure = { RequestResult.Failure(it.message ?: "Invalid email or password") }
            )
        }
    }

    fun logout() {
        auth.signOut()
        _user.value= null
    }

    private suspend fun loginFirebase(email: String, password: String) {
        val responseUser = auth.signInWithEmailAndPassword(email, password).await()
        val userId = responseUser.user?.uid ?: ""
        findByIdFirebase(userId, true)
    }

    private fun getUsers() {
        viewModelScope.launch {
            _users.value = findAllFirebase()
        }
    }

    private suspend fun findAllFirebase(): List<User> {
        val query = db.collection("users")
            .get()
            .await()

        return query.documents.mapNotNull {
            it.toObject(User::class.java)?.apply {
                id = it.id
                location = it.getLocationFromFirebase()
            }
        }
    }

    private fun Map<*, *>?.toLocation(): Location {
        return Location().apply {
            this@toLocation?.let {
                latitude = (it["latitude"] as? Double) ?: 0.0
                longitude = (it["longitude"] as? Double) ?: 0.0
                city = (it["city"] as? String) ?: ""
                country = (it["country"] as? String) ?: ""
            }
        }
    }

    private fun DocumentSnapshot.getLocationFromFirebase(): Location {
        val locMap = this.get("location") as? Map<*, *>
        return locMap.toLocation()
    }

}