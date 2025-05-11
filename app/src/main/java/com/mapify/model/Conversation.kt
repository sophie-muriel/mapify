package com.mapify.model

data class Conversation(
    val id: String,
    val participants: List<User>,
    val messages: List<Message>,
    var isRead: Boolean = false
)