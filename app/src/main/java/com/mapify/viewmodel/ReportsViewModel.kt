package com.mapify.viewmodel

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import calculateDistanceMeters
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.mapify.model.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.ktx.Firebase
import com.mapify.model.Category
import com.mapify.model.Comment
import com.mapify.model.Location
import com.mapify.model.ReportFilters
import com.mapify.model.ReportStatus
import com.mapify.utils.RequestResult
import fetchUserLocation
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class ReportsViewModel: ViewModel() {

    private val db = Firebase.firestore

    private val _reports = MutableStateFlow(emptyList<Report>())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _reportRequestResult = MutableStateFlow<RequestResult?>(null)
    val reportRequestResult: StateFlow<RequestResult?> = _reportRequestResult.asStateFlow()

    private val _filteredReports = MutableStateFlow(emptyList<Report>())
    val filteredReports: StateFlow<List<Report>> = _filteredReports.asStateFlow()

    private val _searchFilters = MutableStateFlow(ReportFilters())
    val searchFilters: StateFlow<ReportFilters> = _searchFilters.asStateFlow()

    private val _createdReportId = MutableStateFlow<String?>(null)
    val createdReportId: StateFlow<String?> = _createdReportId.asStateFlow()

    private val _currentReport = MutableStateFlow<Report?>(null)
    val currentReport: StateFlow<Report?> = _currentReport.asStateFlow()

    private var reportListener: ListenerRegistration? = null
    private var currentReportListener: ListenerRegistration? = null

    init{
        listenToReportsRealtime()
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

    private fun listenToReportsRealtime() {
        reportListener?.remove()
        reportListener = db.collection("reports")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    error?.printStackTrace()
                    return@addSnapshotListener
                }
                val reportsList = snapshot.documents.mapNotNull { document ->
                    try {
                        deserializeReport(document)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                _reports.value = reportsList
            }
    }

    fun listenToCurrentReportRealTime(reportId: String) {
        currentReportListener?.remove()
        currentReportListener = db.collection("reports")
            .document(reportId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    error?.printStackTrace()
                    return@addSnapshotListener
                }
                try {
                    val updatedReport = deserializeReport(snapshot)
                    _currentReport.value = updatedReport
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        reportListener?.remove()
        currentReportListener?.remove()
        resetCurrentReport()
        resetReports()
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

    fun update(updatedReport: Report, action: Int = 1) {
        val (successMessage, errorMessage) = when (action) {
            1 -> "Report updated successfully" to "Error updating report"
            2 -> "Comment posted successfully" to "Error posting comment"
            3 -> "Report verified successfully" to "Error verifying report"
            4 -> "Report rejected, rejection message sent" to "Error rejecting report"
            5 -> "Report boosted" to "Error boosting report"
            else -> "Operation completed" to "Unknown error"
        }
        viewModelScope.launch {
            _reportRequestResult.value = RequestResult.Loading
            _reportRequestResult.value = kotlin.runCatching {
                updateFirebase(updatedReport)
                findByIdFirebase(updatedReport.id)
            }.fold(
                onSuccess = {
                    _currentReport.value = it
                    RequestResult.Success(successMessage)
                },
                onFailure = {
                    RequestResult.Failure(errorMessage)
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
                .fold(
                onSuccess = {
                    RequestResult.Success("Report deleted successfully")
                },
                onFailure = {
                    RequestResult.Failure(it.message ?: "Error deleting report")
                }
            )
        }
    }

    fun delete(reportId: String) {
        viewModelScope.launch {
            _reportRequestResult.value = RequestResult.Loading
            val result = runCatching {
                deleteFirebase(reportId)
            }
            _reportRequestResult.value = result.fold(
                onSuccess = { RequestResult.Success("Report deleted successfully") },
                onFailure = { RequestResult.Failure(it.message ?: "Error deleting report") }
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

    fun removeCurrentReportListener() {
        currentReportListener?.remove()
        currentReportListener = null
    }

    fun resetCurrentReport() {
        if (_currentReport.value != null){
            _currentReport.value = null
        }
    }

    fun resetReports() {
        if (_reports.value.isNotEmpty()){
            _reports.value = emptyList()
        }
    }

    fun resetReportsListener() {
        reportListener?.remove()
        reportListener = null
    }

    fun restartReportsRealtime() {
        resetReportsListener()
        listenToReportsRealtime()
    }

    fun getReportsWithFilters(
        filters: ReportFilters,
        userId: String
    ) {
        viewModelScope.launch {
            _searchFilters.value = filters
            _reportRequestResult.value = RequestResult.Loading

            val result = runCatching {
                getWithFiltersFirebase(filters, userId)
            }

            _reportRequestResult.value = result.fold(
                onSuccess = {
                    _filteredReports.value = it
                    RequestResult.Success("Filters applied successfully")
                },
                onFailure = {
                    RequestResult.Failure(it.localizedMessage ?: "Error applying filters")
                }
            )
        }
    }

    private suspend fun getWithFiltersFirebase(
        filters: ReportFilters,
        userId: String
    ): List<Report> {
        var query = db.collection("reports")
            .whereEqualTo("isDeleted", false)

        if (filters.onlyPriority) {
            query = query.whereEqualTo("isHighPriority", true)
        }

        if (filters.onlyResolved) {
            query = query.whereEqualTo("isResolved", true)
        }

        if (filters.onlyVerified) {
            query = query.whereEqualTo("status", "VERIFIED")
        }

        if (filters.onlyMyPosts) {
            query = query.whereEqualTo("userId", userId)
        }

        if (filters.onlyThisDate) {
            val endDate = filters.thisDate + "T23:59:59.999999"

            query = query.whereGreaterThanOrEqualTo("date", filters.thisDate)
                .whereLessThanOrEqualTo("date", endDate)
        }

        val snapshot = query.get().await()

        return snapshot.documents.mapNotNull { document ->
            try {
                deserializeReport(document)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun reportsWithinDistance(
        reports: List<Report>,
        context: Context,
        filterDistance: Double
    ): List<Report> {
        val userLocation = fetchUserLocation(context) ?: return emptyList()

        return reports.filter { report ->
            report.location?.let { reportLocation ->
                val calculatedDistance = calculateDistanceMeters(
                    lat1 = reportLocation.latitude,
                    lon1 = reportLocation.longitude,
                    lat2 = userLocation.latitude,
                    lon2 = userLocation.longitude
                )
                calculatedDistance <= (filterDistance * 1000)
            } ?: false
        }
    }
    
    fun clearFilters() {
        _filteredReports.value = emptyList()
        _searchFilters.value = ReportFilters()
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
            },
            "isDeleted" to report.isDeleted,
            "isHighPriority" to report.isHighPriority
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
            comments = parseCommentsListFromMap(document.get("comments")),
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