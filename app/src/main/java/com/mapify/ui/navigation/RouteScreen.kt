package com.mapify.ui.navigation

import kotlinx.serialization.Serializable

sealed class RouteScreen {

    @Serializable
    data object Home : RouteScreen()

    @Serializable
    data object Login : RouteScreen()

    @Serializable
    data object Registration : RouteScreen()

    @Serializable
    data object CreateReport : RouteScreen()

    @Serializable
    data object ReportLocation : RouteScreen()

    @Serializable
    data object ReportView : RouteScreen()

    @Serializable
    data object Profile : RouteScreen()

    @Serializable
    data object Explore : RouteScreen()

}