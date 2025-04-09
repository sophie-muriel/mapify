package com.mapify.model

import java.time.LocalDateTime

class Message(
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: LocalDateTime,
    val isRead: Boolean = false,
    val profileImageUrl: String? = null
)