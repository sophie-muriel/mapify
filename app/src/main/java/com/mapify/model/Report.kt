package com.mapify.model

import android.location.Location
import java.time.LocalDate
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
    var priorityCounter: Int = 0,
    var rejectionDate: LocalDateTime? = null,
    var isDeletedManually: Boolean = false,
    var rejectionMessage: String? = null,
    var reportBoosters: MutableList<String> = mutableListOf(),
    var comments: MutableList<Comment> = mutableListOf()
) {
    init {
        require(images.size in 1..5) { "A report must have between 1 and 5 images." }
    }

    val isHighPriority: Boolean
        get() = priorityCounter > 20

    val remainingDaysToDeletion: Int
        get() {
            val today = LocalDate.now()
            val rejection = rejectionDate?.toLocalDate() ?: return -1

            return when (rejection) {
                today -> 5
                today.minusDays(1) -> 4
                today.minusDays(2) -> 3
                today.minusDays(3) -> 2
                today.minusDays(4) -> 1
                else -> 0
            }
        }

    val isDeleted: Boolean
        get() = isDeletedManually || (remainingDaysToDeletion == 0 && rejectionDate != null)
}