package com.mapify.ui.users.tabs

import HandleLocationPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mapify.ui.components.Map
import com.mapify.ui.navigation.LocalMainViewModel

@Composable
fun HomeTab(
    navigateToDetail: (String) -> Unit
) {
    val reportsViewModel = LocalMainViewModel.current.reportsViewModel
    val reports by reportsViewModel.reports.collectAsState()

    HandleLocationPermission(
        onPermissionGranted = {
            Map(
                navigateToDetail = navigateToDetail,
                reports = reports
            )
        }
    )
}