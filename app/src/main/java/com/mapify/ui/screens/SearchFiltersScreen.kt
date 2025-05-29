package com.mapify.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.ui.components.SimpleTopBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mapify.model.ReportFilters
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing
import com.mapify.utils.RequestResultEffectHandler
import com.mapify.utils.SharedPreferencesUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFiltersScreen(
    navigateBack: () -> Unit
) {

    val context = LocalContext.current
    val reportsViewModel = LocalMainViewModel.current.reportsViewModel
    val userId = SharedPreferencesUtils.getPreference(context)["userId"]
    val reportRequestResult by reportsViewModel.reportRequestResult.collectAsState()
    val searchFilters by reportsViewModel.searchFilters.collectAsState()
    var isLoading = rememberSaveable { mutableStateOf(false) }

    var priorityChecked by rememberSaveable { mutableStateOf(searchFilters.onlyPriority) }
    var resolvedChecked by rememberSaveable { mutableStateOf(searchFilters.onlyResolved) }
    var verifiedChecked by rememberSaveable { mutableStateOf(searchFilters.onlyVerified) }
    var myPostsChecked by rememberSaveable { mutableStateOf(searchFilters.onlyMyPosts) }
    var datePressed by rememberSaveable { mutableStateOf(false) }
    var dateSelected by rememberSaveable { mutableStateOf(searchFilters.onlyThisDate) }
    var distancePressed by rememberSaveable { mutableStateOf(false) }
    var distanceSelected by rememberSaveable { mutableStateOf(searchFilters.onlyThisDistance) }
    val datePikerState = rememberDatePickerState(
        initialSelectedDateMillis = searchFilters.thisDate.takeIf { it.isNotEmpty() }?.let {
            LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atStartOfDay(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli()
        }
    )
    var formattedDate by rememberSaveable { mutableStateOf(searchFilters.thisDate) }
    var sliderPosition by rememberSaveable { mutableFloatStateOf(searchFilters.thisDistance.toFloat()) }
    
    Scaffold(
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(id = R.string.search_filters),
                navIconVector = Icons.AutoMirrored.Filled.ArrowBack,
                navIconDescription = stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = {
                    navigateBack()
                },
                actions = false
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
               onChangeDatePressed = { datePressed = it },
               dateSelected = dateSelected,
               distancePressed = distancePressed,
               onChangeDistancePressed = { distancePressed = it },
               distanceSelected = distanceSelected
           )
            Spacer(modifier = Modifier.height(Spacing.Sides))

            Buttons(
                onClickApplyFilters = {
                    val filters = ReportFilters(
                        onlyPriority = priorityChecked,
                        onlyResolved = resolvedChecked,
                        onlyVerified = verifiedChecked,
                        onlyMyPosts = myPostsChecked,
                        onlyThisDate = dateSelected && formattedDate.isNotBlank(),
                        thisDate = formattedDate,
                        onlyThisDistance = distanceSelected && sliderPosition != 0f,
                        thisDistance = sliderPosition.toDouble()
                    )
                    if (userId != null) {
                        reportsViewModel.getReportsWithFilters(filters, userId)
                        Log.d("SearchFilters-1", sliderPosition.toDouble().toString())
                    }
                },
                onClickCleanFilters = {
                    if(priorityChecked || resolvedChecked || verifiedChecked || myPostsChecked || dateSelected || distanceSelected){
                        reportsViewModel.clearFilters()
                        priorityChecked = false
                        resolvedChecked = false
                        verifiedChecked = false
                        myPostsChecked = false
                        datePikerState.selectedDateMillis = null
                        dateSelected = false
                        sliderPosition = 0f
                        distanceSelected = false
                        Toast.makeText(context, "Filters cleaned", Toast.LENGTH_SHORT).show()
                    }
                },
                isLoading = isLoading
            )
            RequestResultEffectHandler(
                requestResult = reportRequestResult,
                context = context,
                isLoading = isLoading,
                onResetResult = { reportsViewModel.resetReportRequestResult() },
                onNavigate = {
                    navigateBack()
                }
            )
        }

        val date = stringResource(id = R.string.selected_date) + " "
        val distance = stringResource(id = R.string.select_distance) + " "
        val distanceKm = " " + stringResource(id = R.string.kilo_meter)

        if(datePressed){
            DatePickerForFilter(
                onDismissRequest = { datePressed = false },
                datePickerState = datePikerState,
                onClickOk = {
                    formattedDate = datePikerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))
                    } ?: ""
                    datePressed = false
                    Toast.makeText(
                        context,
                        date + formattedDate,
                        Toast.LENGTH_SHORT
                    ).show()
                    dateSelected = true
                },
                onClickCancel = { datePressed = false }
            )
        }else if(distancePressed){
            DistanceSelectionDialog(
                onClose = {
                    distancePressed = false
                    if(sliderPosition != 0f){
                        Toast.makeText(
                            context,
                            distance + sliderPosition.toString().dropLast(2) + distanceKm,
                            Toast.LENGTH_SHORT
                        ).show()
                        distanceSelected = true
                    }else{
                        distanceSelected = false
                    }
                },
                sliderPosition = sliderPosition,
                onSliderPositionChange = { sliderPosition = it.toInt().toFloat() },
                onCancel = {
                    if(!distanceSelected){
                        sliderPosition = 0f
                    }
                    distancePressed = false

                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class CustomDateFormatter : DatePickerFormatter {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    private val monthYearFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

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
    dateSelected: Boolean,
    onChangeDatePressed: (Boolean) -> Unit,
    distancePressed: Boolean,
    distanceSelected: Boolean,
    onChangeDistancePressed: (Boolean) -> Unit
){
    FilterRow(
        icon = Icons.Filled.Star,
        description = stringResource(id = R.string.star_icon),
        text = stringResource(id = R.string.priority_filter),
        status = priorityChecked,
        onStatusChange = onChangePriorityCheck,
        isSwitch = true
    )

    FilterRow(
        icon = Icons.Filled.Check,
        description = stringResource(id = R.string.check_icon),
        text = stringResource(id = R.string.resolved_filter),
        status = resolvedChecked,
        onStatusChange = onChangeReSolvedCheck,
        isSwitch = true
    )

    FilterRow(
        icon = Icons.Filled.CheckBox,
        description = stringResource(id = R.string.check_icon),
        text = stringResource(id = R.string.verified_filter),
        status = verifiedChecked,
        onStatusChange = onChangeVerifiedChecked,
        isSwitch = true
    )

    FilterRow(
        icon = Icons.Filled.Person,
        description = stringResource(id = R.string.person_icon),
        text = stringResource(id = R.string.my_posts),
        status = myPostsChecked,
        onStatusChange = onChangeMyPostsCheck,
        isSwitch = true
    )

    FilterRow(
        icon = Icons.Filled.Today,
        description = stringResource(id = R.string.date_icon),
        text = stringResource(id = R.string.date),
        status = datePressed,
        statusTwo = dateSelected,
        onStatusChange = onChangeDatePressed,
        isSwitch = false
    )

    FilterRow(
        icon = Icons.Outlined.LocationOn,
        description = stringResource(id = R.string.location_icon),
        text = stringResource(id = R.string.distance),
        status = distancePressed,
        statusTwo = distanceSelected,
        onStatusChange = onChangeDistancePressed,
        isSwitch = false
    )
}

@Composable
fun FilterRow(
    icon: ImageVector,
    description: String,
    text: String,
    status: Boolean,
    statusTwo: Boolean = false,
    onStatusChange: (Boolean) -> Unit,
    isSwitch: Boolean
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Spacing.Sides,
                end = 48.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = when{
                    isSwitch && status -> MaterialTheme.colorScheme.primary
                    !isSwitch && statusTwo -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.secondary
                }
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
            IconButton(onClick = { onStatusChange(!status) }) {
                Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(id = R.string.play_arrow_icon),
                tint = MaterialTheme.colorScheme.secondary
            )}
        }
    }
    Spacer(modifier = Modifier.height(Spacing.Inline))
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
            TextButton(onClick = onClickCancel) {
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
fun DistanceSelectionDialog(
    onClose: () -> Unit,
    sliderPosition: Float,
    onSliderPositionChange: (Float) -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = { onCancel() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Spacer(modifier = Modifier.height(Spacing.Sides))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)){
                Text(
                    text = stringResource(id = R.string.select_distance),
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(
                        horizontal = Spacing.Sides,
                        vertical = Spacing.Small
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(id = R.string.distance_km),
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(
                        horizontal = Spacing.Sides,
                        vertical = Spacing.Small
                    ),
                    style = MaterialTheme.typography.headlineMedium
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = Spacing.Sides),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Inline)
                ){
                    Slider(
                        value = sliderPosition,
                        onValueChange = onSliderPositionChange,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        steps = 9,
                        valueRange = 0f..10f
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.Sides),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f)){
                        Text(text = sliderPosition.toString().dropLast(2) + " KM")
                    }

                    TextButton(onClick = onCancel) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        modifier = Modifier
                            .wrapContentSize()
                            .height(40.dp),
                        enabled = true,
                        onClick = onClose,
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
                }

            }

            Spacer(modifier = Modifier.height(Spacing.Sides))
        }
    }
}

@Composable
fun Buttons(
    onClickApplyFilters: () -> Unit,
    onClickCleanFilters: () -> Unit,
    isLoading: MutableState<Boolean>
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides),
        verticalArrangement = Arrangement.spacedBy(Spacing.Sides)

    ){
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Sides)
                .height(40.dp),
            enabled = true,
            onClick = onClickApplyFilters,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            if (isLoading.value){
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }else{
                Text(
                    text = stringResource(id = R.string.apply_filters),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Sides)
                .height(40.dp),
            enabled = true,
            onClick = onClickCleanFilters,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
        ) {
            Text(
                text = stringResource(id = R.string.clean_filters),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

