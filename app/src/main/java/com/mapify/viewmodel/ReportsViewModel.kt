package com.mapify.viewmodel

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

    private val _createdReportId = MutableStateFlow<String?>(null)
    val createdReportId: StateFlow<String?> = _createdReportId.asStateFlow()

    private val _currentReport = MutableStateFlow<Report?>(null)
    val currentReport: StateFlow<Report?> = _currentReport.asStateFlow()

    init{
        getReports()
    }
    
    fun create(report: Report) {
        viewModelScope.launch {
            _reportRequestResult.value = RequestResult.Loading

            val result = runCatching {
                createFirebase(report)
            }

            result.fold(
                onSuccess = { id ->
                    _createdReportId.value = id
                    _reportRequestResult.value = RequestResult.Success("Report created successfully")
                },
                onFailure = { e ->
                    _reportRequestResult.value = RequestResult.Failure(e.message ?: "Error creating report")
                }
            )
        }
    }

    private suspend fun createFirebase(report: Report): String{
        val reportMap = mapReport(report)
        val createdReportDocument = db.collection("reports")
            .add(reportMap)
            .await()
        return createdReportDocument.id
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
                val report = deserializeReport(document)
                report
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun findById(reportId: String) {
        viewModelScope.launch {
            _currentReport.value = findByIdFirebase(reportId)
        }
    }

    private suspend fun findByIdFirebase(reportId: String): Report {
        val query = db.collection("reports")
            .document(reportId)
            .get()
            .await()

        return deserializeReport(query)
    }

    fun update(updatedReport: Report) {
        viewModelScope.launch {
            _reportRequestResult.value = RequestResult.Loading
            _reportRequestResult.value = kotlin.runCatching {
                updateFirebase(updatedReport)
                findByIdFirebase(updatedReport.id)
            }.fold(
                onSuccess = {
                    _currentReport.value = it
                    RequestResult.Success("Report updated successfully")
                },
                onFailure = {
                    RequestResult.Failure(it.message ?: "Error updating report")
                }
            )
        }
    }

    private suspend fun updateFirebase(updatedReport: Report) {
        db.collection("reports")
            .document(updatedReport.id)
            .set(mapReport(updatedReport))
            .await()
    }

    fun deactivate(deactivatedReport: Report) {
        deactivatedReport.isDeletedManually = true
        viewModelScope.launch {
            _reportRequestResult.value = RequestResult.Loading
            _reportRequestResult.value = kotlin.runCatching { updateFirebase(deactivatedReport) }
                .fold (
                    onSuccess = { RequestResult.Success("Report deleted successfully") },
                    onFailure = { RequestResult.Failure(it.message?: "Error deleting report") }
                )
        }
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

    fun countComments(reportId: String): Int {
        val report =_reports.value.find { it.id == reportId }
        return report?.comments?.size ?: 0
    }

    fun resetReportRequestResult() {
        _reportRequestResult.value = null
    }

    fun resetCreatedReportId() {
        _createdReportId.value = null
    }

    fun resetCurrentReport() {
        _currentReport.value = null
    }

    fun reloadReports() {
        getReports()
    }

    private fun mapReport(report: Report): Map<String, Any?> {
        return mapOf(
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
    }

    private fun deserializeReport(document: DocumentSnapshot): Report{
        return Report(
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
}