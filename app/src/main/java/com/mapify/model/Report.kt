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
) {
}