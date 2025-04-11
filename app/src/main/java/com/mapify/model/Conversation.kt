package com.mapify.model

data class Conversation(
    val id: String,
    val recipient: User,
    val messages: List<Message>,
    val isRead: Boolean = false
)