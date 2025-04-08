package com.mapify.model

data class Conversation(
    val id: String,
    val sender: String,
    val messages: List<Message>
)