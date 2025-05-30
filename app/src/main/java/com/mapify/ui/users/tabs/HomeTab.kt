package com.mapify.ui.users.tabs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.mapify.ui.components.Map
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.utils.RequestResultEffectHandler
import com.mapify.utils.SharedPreferencesUtils

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeTab(
    navigateToDetail: (String) -> Unit
) {
    val reportsViewModel = LocalMainViewModel.current.reportsViewModel
    val context = LocalContext.current
    val userId = SharedPreferencesUtils.getPreference(context)["userId"]
    val reports by reportsViewModel.reports.collectAsState()
    val reportRequestResult by reportsViewModel.reportRequestResult.collectAsState()

    LaunchedEffect(Unit) {
        reportsViewModel.getReports()
        reportsViewModel.getReportsByUserId(userId ?: "")
    }

    RequestResultEffectHandler(
        requestResult = reportRequestResult,
        context = context,
        onResetResult = { reportsViewModel.resetReportRequestResult() },
        onNavigate = { },
        showsMessage = false
    )

    Map(
        navigateToDetail = navigateToDetail,
        reports = reports,
        isCenteredOnUser = true
    )
}