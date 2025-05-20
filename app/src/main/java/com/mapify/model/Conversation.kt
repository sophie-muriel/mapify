package com.mapify.model

data class Conversation(
    val id: String,
    val participants: List<Participant>,
    val messages: List<Message>,
    var isRead: MutableMap<String, Boolean>
)

data class Participant(
    val id: String,
    val name: String
)