package com.mapify.viewmodel

import androidx.lifecycle.ViewModel
import com.mapify.model.Category
import com.mapify.model.Location
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class ReportsViewModel: ViewModel() {

    private val _reports = MutableStateFlow(emptyList<Report>())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    init{
        _reports.value = getReports()
    }

    fun create(report: Report) {
        _reports.value += report
    }

    fun delete(reportId: String) {
        _reports.value = _reports.value.filter { it.id != reportId }
    }

    fun findById(reportId: String): Report? {
        return _reports.value.find { it.id == reportId }
    }

    fun findByUserId(userId: String): List<Report> {
        return _reports.value.filter { it.userId == userId }
    }

    fun getReports(): List<Report> {
        return listOf(
            Report(
                id = "1",
                title = "Report 1",
                category = Category.SECURITY,
                description = "This is a report",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkmoJWVhxab15KM_FQbk539hzwjN7qhyWeDw&s"),
                location = Location(4.542029, -75.663209, "Colombia", "Armenia"),
                status = ReportStatus.PENDING_VERIFICATION,
                userId = "1",
                date = LocalDateTime.now(),
                rejectionDate = LocalDateTime.now().minusDays(1),
                isDeletedManually = true
            ),
            Report(
                id = "2",
                title = "Report 2",
                category = Category.PETS,
                description = "This is a test report...",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSHtshKCjboh0e9X3dP5l-igYWWA4C8-nSaw&s"),
                location = Location(4.552580, -75.658404, "Colombia", "Armenia"),
                status = ReportStatus.VERIFIED,
                userId = "1",
                date = LocalDateTime.now(),
                isResolved = true,
                priorityCounter = 25
            ),
            Report(
                id = "3",
                title = "Report 3",
                category = Category.INFRASTRUCTURE,
                description = "Another report example",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
                location = Location(4.547182, -75.667272, "Colombia", "Armenia"),
                status = ReportStatus.VERIFIED,
                userId = "2",
                date = LocalDateTime.now().minusHours(13)
            ),
            Report(
                id = "4",
                title = "Report 4",
                category = Category.COMMUNITY,
                description = "Report about illegal dumping near the river.",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
                location = Location(4.544938, -75.657802, "Colombia", "Armenia"),
                status = ReportStatus.VERIFIED,
                userId = "3",
                date = LocalDateTime.now().minusHours(3),
                isResolved = false,
                priorityCounter = 10
            ),
            Report(
                id = "5",
                title = "Report 5",
                category = Category.SECURITY,
                description = "Potholes causing problems in traffic.",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
                location = Location(4.535901, -75.669179, "Colombia", "Armenia"),
                status = ReportStatus.PENDING_VERIFICATION,
                userId = "4",
                date = LocalDateTime.now().minusDays(1),
                isResolved = false,
                priorityCounter = 3,
                rejectionDate = LocalDateTime.now().minusDays(2)
            ),
            Report(
                id = "6",
                title = "Report 6",
                category = Category.PETS,
                description = "Lost dog seen in the neighborhood.",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
                location = Location(4.532393, -75.673202, "Colombia", "Armenia"),
                status = ReportStatus.VERIFIED,
                userId = "5",
                date = LocalDateTime.now().minusMinutes(45),
                isResolved = true,
                priorityCounter = 15,
                isDeletedManually = true
            ),
            Report(
                id = "7",
                title = "Report 7",
                category = Category.SECURITY,
                description = "This is a report",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkmoJWVhxab15KM_FQbk539hzwjN7qhyWeDw&s"),
                location = Location(4.539816, -75.670499, "Colombia", "Armenia"),
                status = ReportStatus.PENDING_VERIFICATION,
                userId = "1",
                date = LocalDateTime.now(),
                rejectionDate = LocalDateTime.now().minusDays(1)
            ),
            Report(
                id = "8",
                title = "Report 8",
                category = Category.PETS,
                description = "This is a test report...",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSHtshKCjboh0e9X3dP5l-igYWWA4C8-nSaw&s"),
                location = Location(4.537453, -75.678245, "Colombia", "Armenia"),
                status = ReportStatus.VERIFIED,
                userId = "1",
                date = LocalDateTime.now(),
                isResolved = true,
                priorityCounter = 25
            )
        )
    }
}