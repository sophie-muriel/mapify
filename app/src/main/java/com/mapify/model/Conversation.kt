package com.mapify.model

data class Conversation(
    val sender: String,
    val messages: List<Message>
)