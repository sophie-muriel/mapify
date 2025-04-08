package com.mapify.ui.screens

import android.content.res.Resources.Theme
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mapify.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFiltersScreen(
    navigateToExplore: () -> Unit
) {

    var priorityChecked by rememberSaveable { mutableStateOf(false) }
    var resolvedChecked by rememberSaveable { mutableStateOf(false) }
    var verifiedChecked by rememberSaveable { mutableStateOf(false) }
    var myPostsChecked by rememberSaveable { mutableStateOf(false) }
    var datePressed by rememberSaveable { mutableStateOf(false) }
    var distancePressed by rememberSaveable { mutableStateOf(false) }
    val datePikerState = rememberDatePickerState()
    var formattedDate by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

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
               onChangeVerifiedChecked = { verifiedChecked = it },
               datePressed = datePressed,
               onChangeDatePressed = { datePressed = true },
               distancePressed = distancePressed,
               onChangeDistancePressed = { distancePressed = it }
           )
        }

        val date = stringResource(id = R.string.selected_date) + " "
        if(datePressed){
            DatePickerForFilter(
                onDismissRequest = { datePressed = false },
                datePickerState = datePikerState,
                onClickOk = {
                    formattedDate = datePikerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                            .format(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault()))
                    } ?: ""
                    datePressed = false
                    Toast.makeText(
                        context,
                        date + formattedDate,
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onClickCancel = { datePressed = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class CustomDateFormatter : DatePickerFormatter {

    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())
    private val monthYearFormatter = DateTimeFormatter.ofPattern("MM/yyyy", Locale.getDefault())

    override fun formatDate(
        dateMillis: Long?,
        locale: CalendarLocale,
        forContentDescription: Boolean
    ): String {
        return dateMillis?.let {
            val date = Instant.ofEpochMilli(it)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
            dateFormatter.format(date)
        } ?: ""
    }

    override fun formatMonthYear(monthMillis: Long?, locale: CalendarLocale): String {
        return monthMillis?.let {
            val date = Instant.ofEpochMilli(it)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
            monthYearFormatter.format(date)
        } ?: ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerForFilter(
    onDismissRequest: () -> Unit,
    datePickerState: DatePickerState,
    onClickOk: () -> Unit,
    onClickCancel: () -> Unit
){
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                modifier = Modifier
                    .wrapContentSize()
                    .height(40.dp),
                enabled = datePickerState.selectedDateMillis != null,
                onClick = {
                    onClickOk()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.ok_button),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(Spacing.Sides))
        },
        dismissButton = {
            TextButton(
                onClick = onClickCancel
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    ){
        DatePicker(
            state = datePickerState,
            dateFormatter = CustomDateFormatter(),
        )
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
    onChangeVerifiedChecked: (Boolean) -> Unit,
    datePressed: Boolean,
    onChangeDatePressed: (Boolean) -> Unit,
    distancePressed: Boolean,
    onChangeDistancePressed: (Boolean) -> Unit
){
    FilterRow(
        icon = Icons.Filled.Star,
        description = stringResource(id = R.string.star_icon),
        tint = MaterialTheme.colorScheme.primary,
        stringResource(id = R.string.priority_filter),
        status = priorityChecked,
        onStatusChange = onChangePriorityCheck,
        isSwitch = true
    )

    FilterRow(
        icon = Icons.Filled.Check,
        description = stringResource(id = R.string.check_icon),
        tint = MaterialTheme.colorScheme.secondary,
        text = stringResource(id = R.string.resolved_filter),
        status = resolvedChecked,
        onStatusChange = onChangeReSolvedCheck,
        isSwitch = true
    )

    FilterRow(
        icon = Icons.Filled.CheckBox,
        description = stringResource(id = R.string.check_icon),
        tint = MaterialTheme.colorScheme.primary,
        text = stringResource(id = R.string.verified_filter),
        status = verifiedChecked,
        onStatusChange = onChangeVerifiedChecked,
        isSwitch = true
    )

    FilterRow(
        icon = Icons.Filled.Person,
        description = stringResource(id = R.string.person_icon),
        tint = MaterialTheme.colorScheme.secondary,
        text = stringResource(id = R.string.my_posts),
        status = myPostsChecked,
        onStatusChange = onChangeMyPostsCheck,
        isSwitch = true
    )

    FilterRow(
        icon = Icons.Filled.Today,
        description = stringResource(id = R.string.date_icon),
        tint = MaterialTheme.colorScheme.secondary,
        text = stringResource(id = R.string.date),
        status = datePressed,
        onStatusChange = onChangeDatePressed,
        isSwitch = false
    )

    FilterRow(
        icon = Icons.Outlined.LocationOn,
        description = stringResource(id = R.string.location_icon),
        tint = MaterialTheme.colorScheme.secondary,
        text = stringResource(id = R.string.distance),
        status = distancePressed,
        onStatusChange = onChangeDistancePressed,
        isSwitch = false
    )
}

@Composable
fun FilterRow(
    icon: ImageVector,
    description: String,
    tint: Color,
    text: String,
    status: Boolean,
    onStatusChange: (Boolean) -> Unit,
    isSwitch: Boolean
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

        if(isSwitch){
            Switch(
                checked = status,
                onCheckedChange = onStatusChange
            )
        }else{
            IconButton(
                onClick = { onStatusChange(!status) }
            ) {
                Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(id = R.string.play_arrow_icon),
                tint = MaterialTheme.colorScheme.secondary
            )}
        }
    }
    Spacer(modifier = Modifier.height(Spacing.Inline))
}

