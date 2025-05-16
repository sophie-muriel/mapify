package com.mapify.ui.navigation

import com.mapify.model.ReportStatus
import kotlinx.serialization.Serializable

sealed class RouteScreen {

    @Serializable data object Home : RouteScreen()
    @Serializable data object Login : RouteScreen()
    @Serializable data object Registration : RouteScreen()
    @Serializable data class CreateReport(val latitude: Double? = null, val longitude: Double? = null) : RouteScreen()
    @Serializable data class ReportLocation(val latitude: Double? = null, val longitude: Double? = null, val isReadOnly: Boolean = false, val isCenteredOnUser: Boolean = false, val hasPrimaryFab: Boolean = true) : RouteScreen()
    @Serializable data class ReportView(val reportId: String, val reportStatus: ReportStatus? = null) : RouteScreen()
    @Serializable data object Profile : RouteScreen()
    @Serializable data object Settings : RouteScreen()
    @Serializable data object SearchFilters : RouteScreen()
    @Serializable data class EditReport(val reportId: String, val latitude: Double? = null, val longitude: Double? = null) : RouteScreen()
    @Serializable data object SearchContact : RouteScreen()
    @Serializable data class Conversation(val id: String, val isConversation: Boolean) : RouteScreen()

}