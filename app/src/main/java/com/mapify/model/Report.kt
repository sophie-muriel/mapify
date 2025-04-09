package com.mapify.model

import java.time.LocalDateTime

class Report(
    var id: String,
    var title: String,
    var category: Category,
    var description: String,
    var images: List<String>,
    var location: Location?,
    var status: ReportStatus,
    var userId: String,
    var date: LocalDateTime,
    var isResolved: Boolean = false,
    var priorityCounter: Int = 0
) {
    init {
        require(images.size in 1..5) { "A report must have between 1 and 5 images." }
    }

    val isHighPriority: Boolean
        get() = priorityCounter > 20
}