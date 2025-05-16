package com.mapify.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.mapify.model.Category
import com.mapify.model.Comment
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import com.mapify.model.User
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

    fun edit(updatedReport: Report) {
        _reports.value = _reports.value.map { report ->
            if (report.id == updatedReport.id) updatedReport else report
        }
    }

    fun deactivate(deactivatedReport: Report) {
        deactivatedReport.isDeletedManually = true
        _reports.value = _reports.value.map { report ->
            if (report.id == deactivatedReport.id) deactivatedReport else report
        }
    }

    fun count(): Int {
        return _reports.value.size
    }

    fun countComments(reportId: String): Int {
        val report =_reports.value.find { it.id == reportId }
        return report?.comments?.size ?: 0
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

    private fun getReports(): List<Report> {
        val location1 = Location("gps")
        location1.latitude = 4.547765
        location1.longitude = -75.661503

        val location2 = Location("gps")
        location2.latitude = 4.543147
        location2.longitude = -75.658812

        val location3 = Location("gps")
        location3.latitude = 4.542909
        location3.longitude = -75.663342

        val location4 = Location("gps")
        location4.latitude = 4.546373
        location4.longitude = -75.667055

        val location5 = Location("gps")
        location5.latitude = 4.536084
        location5.longitude = -75.668962

        val comments1 = mutableListOf<Comment>(
            Comment(
                id = "1",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque.",
                userId = "1",
                date = LocalDateTime.now()
            ),
            Comment(
                id = "2",
                content = "Lorem ipsum dolor sit amet",
                userId = "2",
                date = LocalDateTime.now()
            ),
            Comment(
                id = "3",
                content = "Lorem ipsum dolor sit amet",
                userId = "3",
                date = LocalDateTime.now()
            )
        )

        val comments2 = mutableListOf<Comment>(
            Comment(
                id = "1",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque.",
                userId = "1",
                date = LocalDateTime.now()
            )
        )

        val comments3 = mutableListOf<Comment>(
            Comment(
                id = "1",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque.",
                userId = "1",
                date = LocalDateTime.now()
            ),
            Comment(
                id = "2",
                content = "Lorem ipsum dolor sit amet",
                userId = "2",
                date = LocalDateTime.now()
            )
        )

        val comments4 = mutableListOf<Comment>(
            Comment(
                id = "1",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque.",
                userId = "1",
                date = LocalDateTime.now()
            ),
            Comment(
                id = "2",
                content = "Lorem ipsum dolor sit amet",
                userId = "2",
                date = LocalDateTime.now()
            ),
            Comment(
                id = "3",
                content = "Lorem ipsum dolor sit amet",
                userId = "2",
                date = LocalDateTime.now()
            ),
            Comment(
                id = "4",
                content = "Lorem ipsum dolor sit amet",
                userId = "1",
                date = LocalDateTime.now()
            )
        )

        return mutableListOf(
            Report(
                id = "1",
                title = "Report 1",
                category = Category.SECURITY,
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque. " +
                        "Donec et tortor scelerisque, ornare mi et, tempus sem. Maecenas ullamcorper nulla vel arcu malesuada consectetur. " +
                        "Donec sed pharetra sapien. Nam vitae mi eleifend ex pellentesque vulputate ac in elit." +
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque. " +
                        "Donec et tortor scelerisque, ornare mi et, tempus sem. Maecenas ullamcorper nulla vel arcu malesuada consectetur. " +
                        "Donec sed pharetra sapien. Nam vitae mi eleifend ex pellentesque vulputate ac in elit.",
                images = listOf(
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkmoJWVhxab15KM_FQbk539hzwjN7qhyWeDw&s",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOvSWqWExnQHszC2ZfSLd-xZNC94pRxMO7ag&s"
                ),
                location = location1,
                status = ReportStatus.PENDING_VERIFICATION,
                userId = "1",
                date = LocalDateTime.now(),
                priorityCounter = 10,
                comments = comments1
            ),
            Report(
                id = "2",
                title = "Report 2",
                category = Category.PETS,
                description = "This is an embedded test report to test the pets category and the resolved flag and verified status",
                images = listOf(
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSHtshKCjboh0e9X3dP5l-igYWWA4C8-nSaw&s",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcThXTf5MoQt2F4rJ9lnIRpA-fQ7zZNSRQwtkQ&s",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTFSUC03tbmiZ9hVh3ShKNIJmVyPVk4XIf16A&s"
                ),
                location = location2,
                status = ReportStatus.VERIFIED,
                userId = "1",
                date = LocalDateTime.now(),
                isResolved = true,
                priorityCounter = 25,
                comments = comments2
            ),
            Report(
                id = "3",
                title = "Report 3",
                category = Category.INFRASTRUCTURE,
                description = "Etiam tristique, risus ac pellentesque ullamcorper, mauris nisl tincidunt dui, sit amet porttitor eros nisl a dolor. " +
                        "Mauris eu sapien tincidunt, pulvinar leo a, tincidunt orci. In leo justo, hendrerit at convallis nec, semper in neque. Nunc " +
                        "at metus eros. Aliquam erat volutpat. Sed nec faucibus leo, quis cursus nisl.",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
                location = location3,
                status = ReportStatus.VERIFIED,
                userId = "2",
                date = LocalDateTime.now(),
                priorityCounter = 11,
                comments = comments3
            ),

            Report(
                id = "4",
                title = "Report 4",
                category = Category.COMMUNITY,
                description = "Etiam tristique, risus ac pellentesque ullamcorper, mauris nisl tincidunt dui, sit amet porttitor eros nisl a dolor.",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
                location = location4,
                status = ReportStatus.VERIFIED,
                userId = "2",
                date = LocalDateTime.now().minusHours(3),
                priorityCounter = 21,
                comments = comments4
            ),
            Report(
                id = "5",
                title = "Report 5",
                category = Category.SECURITY,
                description = "Mauris eu sapien tincidunt, pulvinar leo a, tincidunt orci. In leo justo, hendrerit at convallis nec, semper in neque. Nunc " +
                        "at metus eros. Aliquam erat volutpat. Sed nec faucibus leo, quis cursus nisl.",
                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
                location = location5,
                status = ReportStatus.PENDING_VERIFICATION,
                userId = "2",
                date = LocalDateTime.now().minusDays(1),
                isResolved = true,
                priorityCounter = 3
            )
        )
    }
}