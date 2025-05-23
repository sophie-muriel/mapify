package com.mapify.ui.users.tabs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mapify.ui.components.Map
import com.mapify.ui.navigation.LocalMainViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeTab(
    navigateToDetail: (String) -> Unit
) {
    val reportsViewModel = LocalMainViewModel.current.reportsViewModel
    reportsViewModel.reloadReports()
    val reports by reportsViewModel.reports.collectAsState()

    Map(
        navigateToDetail = navigateToDetail,
        reports = reports,
        isCenteredOnUser = true
    )
}