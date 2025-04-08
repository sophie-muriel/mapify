package com.mapify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.ui.components.SimpleTopBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.mapify.ui.theme.Spacing

@Composable
fun SearchFiltersScreen(
    navigateToExplore: () -> Unit
) {

    var priorityChecked by rememberSaveable { mutableStateOf(false) }
    var solvedChecked by rememberSaveable { mutableStateOf(false) }
    //TODO: Navigation back to ExploreScreen with variables 
    Scaffold(
        topBar = {
            SimpleTopBar(
                Alignment.CenterStart,
                stringResource(id = R.string.search_filters),
                Icons.AutoMirrored.Filled.ArrowBack,
                stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = { navigateToExplore() },
                false
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
           SearchFilters(
               priorityChecked = priorityChecked,
               onChangePriorityCheck = { priorityChecked = it },
               solvedChecked = solvedChecked,
               onChangeSolvedCheck = { solvedChecked = it}
           )
        }
    }
}

@Composable
fun SearchFilters(
    priorityChecked: Boolean,
    onChangePriorityCheck: (Boolean) -> Unit,
    solvedChecked: Boolean,
    onChangeSolvedCheck: (Boolean) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.Sides, end = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = stringResource(id = R.string.star_icon),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = stringResource(id = R.string.priority_filter)
            )
        }

        Switch(
            checked = priorityChecked,
            onCheckedChange = onChangePriorityCheck
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.Sides, end = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = stringResource(id = R.string.check_icon),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = stringResource(id = R.string.resolved_filter)
            )
        }

        Switch(
            checked = solvedChecked,
            onCheckedChange = onChangeSolvedCheck
        )
    }
}