package com.mapify.model

import java.time.LocalDate
import java.time.LocalDateTime

class Report(
    var id: String = "",
    var title: String = "",
    var category: Category = Category.SECURITY,
    var description: String = "",
    var images: List<String> = mutableListOf(),
    var location: Location? = null,
    var status: ReportStatus = ReportStatus.NOT_VERIFIED,
    var userId: String = "",
    var date: LocalDateTime = LocalDateTime.now(),
    var isResolved: Boolean = false,
    var priorityCounter: Int = 0,
    var rejectionDate: LocalDateTime? = null,
    var isDeletedManually: Boolean = false,
    var rejectionMessage: String? = null,
    var deletionMessage: String? = null,
    var reportBoosters: MutableList<String> = mutableListOf(),
    var comments: MutableList<Comment> = mutableListOf(),
    var lastAdminActionDate: LocalDateTime? = null
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

    fun generateDeletionMessage() {
        if (remainingDaysToDeletion == 0 && rejectionDate != null) {
            deletionMessage =
                "Report deleted for not being corrected within 5 days after its rejection."
        }
    }
}