package com.mapify.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mapify.R
import com.mapify.ui.users.navigation.UserRouteTab

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        bottomNavItems().forEach { item ->
            val selected = currentDestination?.hasRoute(item.route::class) == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.iconSelected else item.icon,
                        contentDescription = item.iconDescription,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}

@Composable
private fun bottomNavItems(): List<UserNavigationBar> = listOf(
    navItem(
        route = UserRouteTab.Home,
        outlinedIcon = Icons.Outlined.Home,
        filledIcon = Icons.Filled.Home,
        labelRes = R.string.home_icon
    ),
    navItem(
        route = UserRouteTab.Explore,
        outlinedIcon = Icons.Outlined.Search,
        filledIcon = Icons.Filled.Search,
        labelRes = R.string.search_icon
    ),
    navItem(
        route = UserRouteTab.Notifications,
        outlinedIcon = Icons.Outlined.Notifications,
        filledIcon = Icons.Filled.Notifications,
        labelRes = R.string.notifications_icon
    ),
    navItem(
        route = UserRouteTab.Profile,
        outlinedIcon = Icons.Outlined.AccountCircle,
        filledIcon = Icons.Filled.AccountCircle,
        labelRes = R.string.profile_icon
    )
)

@Composable
private fun navItem(
    route: UserRouteTab,
    outlinedIcon: ImageVector,
    filledIcon: ImageVector,
    labelRes: Int
) = UserNavigationBar(
    route = route,
    icon = outlinedIcon,
    iconSelected = filledIcon,
    iconDescription = stringResource(id = labelRes)
)

data class UserNavigationBar(
    val route: UserRouteTab,
    val icon: ImageVector,
    val iconDescription: String,
    val iconSelected: ImageVector
)