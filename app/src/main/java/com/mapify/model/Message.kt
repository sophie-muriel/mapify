package com.mapify.model

import java.time.LocalDateTime

data class Message(
    val id: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: LocalDateTime
)