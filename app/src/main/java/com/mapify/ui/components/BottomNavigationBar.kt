package com.mapify.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mapify.R
import com.mapify.ui.users.navigation.UserRouteTab
import androidx.compose.runtime.getValue

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {

    val list = listOf<UserNavigationBar>(
        UserNavigationBar(
            icon = Icons.Outlined.Home,
            iconDescription = stringResource(id = R.string.home_icon),
            route = UserRouteTab.Home,
            iconSelected = Icons.Filled.Home
        ),
        UserNavigationBar(
            icon = Icons.Outlined.Search,
            iconDescription = stringResource(id = R.string.search_icon),
            route = UserRouteTab.Explore,
            iconSelected = Icons.Filled.Search
        ),
        UserNavigationBar(
            icon = Icons.Outlined.Notifications,
            iconDescription = stringResource(id = R.string.notifications_icon),
            route = UserRouteTab.Notifications,
            iconSelected = Icons.Filled.Notifications
        ),
        UserNavigationBar(
            icon = Icons.Outlined.QuestionAnswer,
            iconDescription = stringResource(id = R.string.messages_icon),
            route = UserRouteTab.Messages,
            iconSelected = Icons.Filled.QuestionAnswer
        ),
    )

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        list.forEach {
            val isSelected = currentDestination?.route == it.route::class.qualifiedName
            NavigationBarItem(
                onClick = {
                    navController.navigate(it.route){
                        popUpTo(navController.graph.startDestinationId){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) it.icon else it.iconSelected,
                        contentDescription = it.iconDescription,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                selected = isSelected
            )
        }
    }
}

data class UserNavigationBar(
    val route: UserRouteTab,
    val icon: ImageVector,
    val iconDescription: String,
    val iconSelected: ImageVector
)