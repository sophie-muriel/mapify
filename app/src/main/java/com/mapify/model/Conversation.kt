package com.mapify.model

data class Conversation(
    var id: String = "",
    var participants: List<Participant> = emptyList(),
    var messages: List<Message> = emptyList(),
    var isRead: MutableMap<String, Boolean> = mutableMapOf(),
    var deletedFor: MutableList<String> = mutableListOf()
)

data class Participant(
    val id: String = "",
    val name: String = ""
)