package com.mapify.ui.screens

import android.content.res.Resources.Theme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mapify.ui.theme.Spacing

@Composable
fun SearchFiltersScreen(
    navigateToExplore: () -> Unit
) {

    var priorityChecked by rememberSaveable { mutableStateOf(false) }
    var resolvedChecked by rememberSaveable { mutableStateOf(false) }
    var verifiedChecked by rememberSaveable { mutableStateOf(false) }
    var myPostsChecked by rememberSaveable { mutableStateOf(false) }

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
               resolvedChecked = resolvedChecked,
               onChangeReSolvedCheck = { resolvedChecked = it },
               myPostsChecked = myPostsChecked,
               onChangeMyPostsCheck = { myPostsChecked = it },
               verifiedChecked = verifiedChecked,
               onChangeVerifiedChecked = { verifiedChecked = it }
           )
        }
    }
}

@Composable
fun SearchFilters(
    priorityChecked: Boolean,
    onChangePriorityCheck: (Boolean) -> Unit,
    resolvedChecked: Boolean,
    onChangeReSolvedCheck: (Boolean) -> Unit,
    myPostsChecked: Boolean,
    onChangeMyPostsCheck: (Boolean) -> Unit,
    verifiedChecked: Boolean,
    onChangeVerifiedChecked: (Boolean) -> Unit
){
    SwitchFilterRow(
        icon = Icons.Filled.Star,
        description = stringResource(id = R.string.star_icon),
        tint = MaterialTheme.colorScheme.primary,
        stringResource(id = R.string.priority_filter),
        isChecked = priorityChecked,
        onChangeCheck = onChangePriorityCheck
    )

    SwitchFilterRow(
        icon = Icons.Filled.Check,
        description = stringResource(id = R.string.check_icon),
        tint = MaterialTheme.colorScheme.secondary,
        text = stringResource(id = R.string.resolved_filter),
        isChecked = resolvedChecked,
        onChangeCheck = onChangeReSolvedCheck
    )

    SwitchFilterRow(
        icon = Icons.Filled.CheckBox,
        description = stringResource(id = R.string.check_icon),
        tint = MaterialTheme.colorScheme.primary,
        text = stringResource(id = R.string.verified_filter),
        isChecked = verifiedChecked,
        onChangeCheck = onChangeVerifiedChecked
    )

    SwitchFilterRow(
        icon = Icons.Filled.Person,
        description = stringResource(id = R.string.person_icon),
        tint = MaterialTheme.colorScheme.secondary,
        text = stringResource(id = R.string.my_posts),
        isChecked = myPostsChecked,
        onChangeCheck = onChangeMyPostsCheck
    )
}

@Composable
fun SwitchFilterRow(
    icon: ImageVector,
    description: String,
    tint: Color,
    text: String,
    isChecked: Boolean,
    onChangeCheck: (Boolean) -> Unit
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
                imageVector = icon,
                contentDescription = description,
                tint = tint
            )

            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = text
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onChangeCheck
        )
    }
    Spacer(modifier = Modifier.height(Spacing.Inline))
}