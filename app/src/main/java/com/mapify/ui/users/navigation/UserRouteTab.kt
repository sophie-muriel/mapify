package com.mapify.ui.users.navigation

import kotlinx.serialization.Serializable

sealed class UserRouteTab {

    @Serializable data object Home: UserRouteTab()
    @Serializable data object Explore: UserRouteTab()
    @Serializable data object Notifications: UserRouteTab()
    @Serializable data object Messages: UserRouteTab()
}