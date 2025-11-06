package com.mapify.ui.users

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mapify.R
import com.mapify.model.ReportStatus
import com.mapify.ui.users.navigation.UserNavigation
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.CreateFAB
import com.mapify.ui.components.SimpleTopBar
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.users.navigation.UserRouteTab

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun HomeScreen(
    initialSelectedTab: Int = 0,
    navigateToCreateReport: () -> Unit,
    navigateToDetail: (String) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToReportView: (String, ReportStatus) -> Unit,
    navigateToSearchFilters: () -> Unit
) {
    val navController = rememberNavController()

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route
    val reportsViewModel = LocalMainViewModel.current.reportsViewModel
    val searchFilters by reportsViewModel.searchFilters.collectAsState()

    val currentTabIndex = when (currentRoute) {
        UserRouteTab.Home::class.qualifiedName -> 0
        UserRouteTab.Explore::class.qualifiedName -> 1
        UserRouteTab.Notifications::class.qualifiedName -> 2
        UserRouteTab.Profile::class.qualifiedName -> 3
        else -> 0
    }
    val selectedTab = rememberSaveable { mutableIntStateOf(currentTabIndex) }
    LaunchedEffect(initialSelectedTab) {
        if (selectedTab.intValue != initialSelectedTab) {
            selectedTab.intValue = initialSelectedTab
            when (initialSelectedTab) {
                0 -> navController.navigate(UserRouteTab.Home) { launchSingleTop = true }
                1 -> navController.navigate(UserRouteTab.Explore) { launchSingleTop = true }
                2 -> navController.navigate(UserRouteTab.Notifications) { launchSingleTop = true }
                3 -> navController.navigate(UserRouteTab.Profile) { launchSingleTop = true }
            }
        }
    }

    val alignment = Alignment.CenterStart
    val actions = true
    val settingsIconVector = Icons.Filled.Settings
    val settingsIconDescription = stringResource(id = R.string.settings_icon)

    var profileEditMode by rememberSaveable { mutableStateOf(false) }
    var profileNameTouched by rememberSaveable { mutableStateOf(false) }
    var profileEmailTouched by rememberSaveable { mutableStateOf(false) }
    var profilePasswordTouched by rememberSaveable { mutableStateOf(false) }
    var profileExitDialogVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            when (currentRoute) {
                UserRouteTab.Home::class.qualifiedName -> {
                    SimpleTopBar(
                        contentAlignment = alignment,
                        text = stringResource(id = R.string.home_screen),
                        actions = actions,
                        firstActionIconVector = settingsIconVector,
                        firstActionIconDescription = settingsIconDescription,
                        firstOnClickAction = { navigateToSettings() }
                    )
                }

                UserRouteTab.Explore::class.qualifiedName -> {
                    SimpleTopBar(
                        contentAlignment = alignment,
                        text = stringResource(id = R.string.explore_screen),
                        actions = actions,
                        firstActionIconVector = Icons.Filled.Search,
                        firstActionIconDescription = stringResource(id = R.string.search_icon),
                        firstOnClickAction = { navigateToSearchFilters() },
                        secondAction = true,
                        secondActionIconVector = settingsIconVector,
                        secondActionIconDescription = settingsIconDescription,
                        secondOnClickAction = { navigateToSettings() },
                        areFiltersActive = searchFilters.areSet
                    )
                }

                UserRouteTab.Notifications::class.qualifiedName -> {
                    SimpleTopBar(
                        contentAlignment = alignment,
                        text = stringResource(id = R.string.notifications),
                        actions = actions,
                        firstActionIconVector = settingsIconVector,
                        firstActionIconDescription = settingsIconDescription,
                        firstOnClickAction = { navigateToSettings() }
                    )
                }

                UserRouteTab.Profile::class.qualifiedName -> {
                    SimpleTopBar(
                        contentAlignment = alignment,
                        text = stringResource(id = R.string.profile),
                        actions = true,
                        firstActionIconVector = if (profileEditMode) Icons.AutoMirrored.Filled.ArrowBack else Icons.Outlined.Edit,
                        firstActionIconDescription = if (profileEditMode) stringResource(id = R.string.back_arrow_icon) else stringResource(id = R.string.edit_icon_description),
                        firstOnClickAction = {
                            if (profileEditMode && (profileNameTouched || profileEmailTouched || profilePasswordTouched)) {
                                profileExitDialogVisible = true
                            } else {
                                profileEditMode = !profileEditMode
                            }
                        },
                        secondAction = true,
                        secondActionIconVector = settingsIconVector,
                        secondActionIconDescription = settingsIconDescription,
                        secondOnClickAction = { navigateToSettings() }
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
            )
        },
        floatingActionButton = {
            when (currentRoute) {
                UserRouteTab.Home::class.qualifiedName, UserRouteTab.Explore::class.qualifiedName -> {
                    CreateFAB(
                        onClick = { navigateToCreateReport() },
                        icon = Icons.Filled.Add,
                        iconDescription = stringResource(id = R.string.add_icon_description)
                    )
                }
            }
        }
    ) { padding ->
        UserNavigation(
            padding = padding,
            navController = navController,
            navigateToDetail = navigateToDetail,
            navigateToReportView = navigateToReportView,
            profileEditMode = profileEditMode,
            onProfileEditModeChange = { profileEditMode = it },
            profileNameTouched = profileNameTouched,
            onProfileNameTouchedChange = { profileNameTouched = it },
            profileEmailTouched = profileEmailTouched,
            onProfileEmailTouchedChange = { profileEmailTouched = it },
            profilePasswordTouched = profilePasswordTouched,
            onProfilePasswordTouchedChange = { profilePasswordTouched = it },
            profileExitDialogVisible = profileExitDialogVisible,
            onProfileExitDialogVisibleChange = { profileExitDialogVisible = it }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}