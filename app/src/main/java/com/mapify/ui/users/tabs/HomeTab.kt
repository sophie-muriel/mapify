package com.mapify.ui.users.tabs

import HandleLocationPermission
import androidx.compose.runtime.Composable
import com.mapify.ui.components.Map

@Composable
fun HomeTab(
    navigateToDetail: (String) -> Unit
) {
    HandleLocationPermission(
        onPermissionGranted = {
            Map(navigateToDetail = navigateToDetail)
        }
    )
}