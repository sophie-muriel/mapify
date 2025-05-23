package com.mapify.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.mapify.model.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.ktx.Firebase
import com.mapify.model.Category
import com.mapify.model.Comment
import com.mapify.model.Location
import com.mapify.model.ReportStatus
import com.mapify.utils.RequestResult
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class ReportsViewModel: ViewModel() {

    private val db = Firebase.firestore

    private val _reports = MutableStateFlow(emptyList<Report>())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _reportRequestResult = MutableStateFlow<RequestResult?>(null)
    val reportRequestResult: StateFlow<RequestResult?> = _reportRequestResult.asStateFlow()

    init{
        getReports()
    }

    fun create(report: Report) {
        viewModelScope.launch {
            _reportRequestResult.value = RequestResult.Loading
            _reportRequestResult.value = kotlin.runCatching { createFirebase(report) }
                .fold(
                    onSuccess = { RequestResult.Success("Report created successfully") },
                    onFailure = { RequestResult.Failure(it.message ?: "Error creating report") }
                )
        }
    }

    private suspend fun createFirebase(report: Report){
        val reportMap = mapOf(
            "id" to report.id,
            "title" to report.title,
            "category" to report.category.name,
            "description" to report.description,
            "images" to report.images,
            "location" to report.location?.let {
                mapOf(
                    "latitude" to it.latitude,
                    "longitude" to it.longitude
                )
            },
            "status" to report.status.name,
            "userId" to report.userId,
            "date" to report.date.toString(),
            "isResolved" to report.isResolved,
            "priorityCounter" to report.priorityCounter,
            "rejectionDate" to report.rejectionDate?.toString(),
            "isDeletedManually" to report.isDeletedManually,
            "rejectionMessage" to report.rejectionMessage,
            "reportBoosters" to report.reportBoosters,
            "comments" to report.comments.map { comment ->
                mapOf(
                    "id" to comment.id,
                    "content" to comment.content,
                    "userId" to comment.userId,
                    "date" to comment.date.toString()
                )
            }
        )

        db.collection("reports")
            .add(reportMap)
            .await()
    }

    fun delete(reportId: String) {
        viewModelScope.launch {
            _reportRequestResult.value = RequestResult.Loading
            _reportRequestResult.value = kotlin.runCatching { deleteFirebase(reportId) }
                .fold (
                    onSuccess = { RequestResult.Success("Report deleted successfully") },
                    onFailure = { RequestResult.Failure(it.message?: "Error deleting report") }
                )
        }
    }

    private suspend fun deleteFirebase(reportId: String) {
        db.collection("reports")
            .document(reportId)
            .delete()
            .await()
    }

    private fun getReports() {
        viewModelScope.launch {
            _reports.value = findAllFirebase()
        }
    }

    private suspend fun findAllFirebase(): List<Report> {
        val query = db.collection("reports")
            .get()
            .await()

        return query.documents.mapNotNull { document ->
            try {
                val report = Report(
                    id = document.id,
                    title = document.getString("title") ?: "",
                    category = document.getString("category")?.let { categoryStr ->
                        Category.entries.firstOrNull { it.name == categoryStr }
                    } ?: Category.SECURITY,
                    description = document.getString("description") ?: "",
                    images = document.get("images") as? List<String> ?: emptyList(),
                    location = document.getLocationFromFirebase(),
                    status = document.getString("status")?.let { statusStr ->
                        ReportStatus.entries.firstOrNull { it.name == statusStr }
                    } ?: ReportStatus.NOT_VERIFIED,
                    userId = document.getString("userId") ?: "",
                    date = document.getString("date")?.let { parseDate(it) } ?: LocalDateTime.now(),
                    isResolved = document.getBoolean("isResolved") ?: false,
                    priorityCounter = (document.getLong("priorityCounter") ?: 0L).toInt(),
                    rejectionDate = document.getString("rejectionDate")?.let { parseDate(it) },
                    isDeletedManually = document.getBoolean("isDeletedManually") ?: false,
                    rejectionMessage = document.getString("rejectionMessage"),
                    reportBoosters = document.get("reportBoosters") as? MutableList<String> ?: mutableListOf(),
                    comments = parseCommentsListFromMap(document.get("comments"))
                )
                report
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
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

    fun findById(reportId: String): Report? {
        return _reports.value.find { it.id == reportId }
    }

    fun resetReportRequestResult() {
        _reportRequestResult.value = null
    }

    private fun Map<*, *>?.toLocation(): Location {
        return Location().apply {
            this@toLocation?.let {
                latitude = (it["latitude"] as? Double) ?: 0.0
                longitude = (it["longitude"] as? Double) ?: 0.0
                city = (it["city"] as? String) ?: ""
                country = (it["country"] as? String) ?: ""
            }
        }
    }

    private fun DocumentSnapshot.getLocationFromFirebase(): Location {
        val locMap = this.get("location") as? Map<*, *>
        return locMap.toLocation()
    }

    private fun parseCommentsListFromMap(rawComments: Any?): MutableList<Comment> {
        return (rawComments as? List<*>)?.mapNotNull { item ->
            val commentMap = item as? Map<*, *> ?: return@mapNotNull null
            Comment(
                id = commentMap["id"] as? String ?: "",
                content = commentMap["content"] as? String ?: "",
                userId = commentMap["userId"] as? String ?: "",
                date = LocalDateTime.parse(commentMap["date"] as? String ?: "")
            )
        }?.toMutableList() ?: mutableListOf()
    }

    private fun parseDate(dateStr: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

//    private fun getReports(): List<Report> {
//        return mutableListOf()
//        val location1 = Location("gps")
//        location1.latitude = 4.547765
//        location1.longitude = -75.661503
//
//        val location2 = Location("gps")
//        location2.latitude = 4.543147
//        location2.longitude = -75.658812
//
//        val location3 = Location("gps")
//        location3.latitude = 4.542909
//        location3.longitude = -75.663342
//
//        val location4 = Location("gps")
//        location4.latitude = 4.546373
//        location4.longitude = -75.667055
//
//        val location5 = Location("gps")
//        location5.latitude = 4.536084
//        location5.longitude = -75.668962
//
//        val comments1 = mutableListOf<Comment>(
//            Comment(
//                id = "1",
//                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque.",
//                userId = "1",
//                date = LocalDateTime.now()
//            ),
//            Comment(
//                id = "2",
//                content = "Lorem ipsum dolor sit amet",
//                userId = "2",
//                date = LocalDateTime.now()
//            ),
//            Comment(
//                id = "3",
//                content = "Lorem ipsum dolor sit amet",
//                userId = "3",
//                date = LocalDateTime.now()
//            )
//        )
//
//        val comments2 = mutableListOf<Comment>(
//            Comment(
//                id = "1",
//                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque.",
//                userId = "1",
//                date = LocalDateTime.now()
//            )
//        )
//
//        val comments3 = mutableListOf<Comment>(
//            Comment(
//                id = "1",
//                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque.",
//                userId = "1",
//                date = LocalDateTime.now()
//            ),
//            Comment(
//                id = "2",
//                content = "Lorem ipsum dolor sit amet",
//                userId = "2",
//                date = LocalDateTime.now()
//            )
//        )
//
//        val comments4 = mutableListOf<Comment>(
//            Comment(
//                id = "1",
//                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque.",
//                userId = "1",
//                date = LocalDateTime.now()
//            ),
//            Comment(
//                id = "2",
//                content = "Lorem ipsum dolor sit amet",
//                userId = "2",
//                date = LocalDateTime.now()
//            ),
//            Comment(
//                id = "3",
//                content = "Lorem ipsum dolor sit amet",
//                userId = "2",
//                date = LocalDateTime.now()
//            ),
//            Comment(
//                id = "4",
//                content = "Lorem ipsum dolor sit amet",
//                userId = "1",
//                date = LocalDateTime.now()
//            )
//        )
//
//        return mutableListOf(
//            Report(
//                id = "1",
//                title = "Report 1",
//                category = Category.SECURITY,
//                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque. " +
//                        "Donec et tortor scelerisque, ornare mi et, tempus sem. Maecenas ullamcorper nulla vel arcu malesuada consectetur. " +
//                        "Donec sed pharetra sapien. Nam vitae mi eleifend ex pellentesque vulputate ac in elit." +
//                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque. " +
//                        "Donec et tortor scelerisque, ornare mi et, tempus sem. Maecenas ullamcorper nulla vel arcu malesuada consectetur. " +
//                        "Donec sed pharetra sapien. Nam vitae mi eleifend ex pellentesque vulputate ac in elit.",
//                images = listOf(
//                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkmoJWVhxab15KM_FQbk539hzwjN7qhyWeDw&s",
//                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOvSWqWExnQHszC2ZfSLd-xZNC94pRxMO7ag&s"
//                ),
//                location = location1,
//                status = ReportStatus.PENDING_VERIFICATION,
//                userId = "1",
//                date = LocalDateTime.now(),
//                priorityCounter = 10,
//                comments = comments1
//            ),
//            Report(
//                id = "2",
//                title = "Report 2",
//                category = Category.PETS,
//                description = "This is an embedded test report to test the pets category and the resolved flag and verified status",
//                images = listOf(
//                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSHtshKCjboh0e9X3dP5l-igYWWA4C8-nSaw&s",
//                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcThXTf5MoQt2F4rJ9lnIRpA-fQ7zZNSRQwtkQ&s",
//                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTFSUC03tbmiZ9hVh3ShKNIJmVyPVk4XIf16A&s"
//                ),
//                location = location2,
//                status = ReportStatus.VERIFIED,
//                userId = "1",
//                date = LocalDateTime.now(),
//                isResolved = true,
//                priorityCounter = 25,
//                comments = comments2
//            ),
//            Report(
//                id = "3",
//                title = "Report 3",
//                category = Category.INFRASTRUCTURE,
//                description = "Etiam tristique, risus ac pellentesque ullamcorper, mauris nisl tincidunt dui, sit amet porttitor eros nisl a dolor. " +
//                        "Mauris eu sapien tincidunt, pulvinar leo a, tincidunt orci. In leo justo, hendrerit at convallis nec, semper in neque. Nunc " +
//                        "at metus eros. Aliquam erat volutpat. Sed nec faucibus leo, quis cursus nisl.",
//                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
//                location = location3,
//                status = ReportStatus.VERIFIED,
//                userId = "2",
//                date = LocalDateTime.now(),
//                priorityCounter = 11,
//                comments = comments3
//            ),
//
//            Report(
//                id = "4",
//                title = "Report 4",
//                category = Category.COMMUNITY,
//                description = "Etiam tristique, risus ac pellentesque ullamcorper, mauris nisl tincidunt dui, sit amet porttitor eros nisl a dolor.",
//                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
//                location = location4,
//                status = ReportStatus.VERIFIED,
//                userId = "2",
//                date = LocalDateTime.now().minusHours(3),
//                priorityCounter = 21,
//                comments = comments4
//            ),
//            Report(
//                id = "5",
//                title = "Report 5",
//                category = Category.SECURITY,
//                description = "Mauris eu sapien tincidunt, pulvinar leo a, tincidunt orci. In leo justo, hendrerit at convallis nec, semper in neque. Nunc " +
//                        "at metus eros. Aliquam erat volutpat. Sed nec faucibus leo, quis cursus nisl.",
//                images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
//                location = location5,
//                status = ReportStatus.PENDING_VERIFICATION,
//                userId = "2",
//                date = LocalDateTime.now().minusDays(1),
//                isResolved = true,
//                priorityCounter = 3
//            )
//        )
//    }
}