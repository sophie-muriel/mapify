package com.mapify.model

import java.time.LocalDateTime

class Comment(
    var id: String,
    var content: String,
    var userId: String,
    var date: LocalDateTime
)