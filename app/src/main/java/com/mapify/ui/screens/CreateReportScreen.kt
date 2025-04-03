package com.mapify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.mapify.ui.components.ReportForm
import com.mapify.ui.theme.Spacing
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import com.mapify.model.Category
import java.time.LocalDateTime
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    navigateToHome: () -> Unit,
    navigateToReportLocation: () -> Unit
){

    var title by rememberSaveable { mutableStateOf("") }
    var titleTouched by rememberSaveable { mutableStateOf(false) }
    val titleError = titleTouched && title.isBlank()

    var dropDownValue by rememberSaveable { mutableStateOf("") }
    var dropDownExpanded by rememberSaveable { mutableStateOf(false) }
    var dropDownTouched by rememberSaveable { mutableStateOf(false) }
    val categories = Category.entries.map { it.displayName }
    val dropDownError = dropDownValue.isBlank()

    var description by rememberSaveable { mutableStateOf("") }
    var descriptionTouched by rememberSaveable { mutableStateOf(false) }
    val descriptionError = descriptionTouched && (description.isBlank() || description.length < 10)

    var location by rememberSaveable { mutableStateOf("") }
    val locationError = false

    var photo by rememberSaveable { mutableStateOf("") }
    var photoTouched by rememberSaveable { mutableStateOf(false) }
    val photoError = photoTouched && photo.isBlank()

    val reportsList = ArrayList<Report>()
    val context = LocalContext.current

    val isKeyboardActive = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Scaffold(
        topBar = {
            if(!isKeyboardActive){
                TopAppBar(
                    modifier = Modifier.padding(horizontal = Spacing.Small),
                    title = {
                        Text(
                            text = "Create Report",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navigateToHome()
                            }
                        )  {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Black Arrow"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                //Location has to be added here later
                                if (title.isNotEmpty() && dropDownValue.isNotEmpty()
                                    && description.isNotEmpty() && photo.isNotEmpty()){
                                    val newReport = Report(
                                        title = title,
                                        category = Category.entries.find { it.displayName == dropDownValue }!!,
                                        description = description,
                                        location = null, //This must be changed
                                        images = listOf(photo),
                                        id = "1",
                                        status = ReportStatus.NOT_VERIFIED,
                                        userId = "1",
                                        date = LocalDateTime.now()
                                    )
                                    reportsList.add(newReport)
                                    val size = reportsList.get(0).category.toString()
                                    Toast.makeText(context, size, Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context, "0", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )  {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = "Check"
                            )
                        }
                    },
                )

            }

        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ReportForm(
                title = title,
                onValueChangeTitle = {
                    title = it
                    titleTouched = true
                },
                titleError = titleError,
                placeHolder = "Category",
                value = dropDownValue,
                onValueChange = {
                    dropDownValue = it
                    dropDownTouched = true
                    dropDownExpanded = false
                },
                dropDownError = dropDownError,
                items = categories,
                isExpanded = dropDownExpanded,
                onExpandedChange = {
                    dropDownExpanded = it
                    dropDownTouched = true
                },
                onDismissRequest = {
                    dropDownExpanded = false
                },
                isTouched = dropDownTouched,
                description = description,
                onValueChangeDescription = {
                    description = it
                    descriptionTouched = true
                },
                descriptionError = descriptionError,
                location = location,
                onValueChangeLocation = {
                    location = it
                },
                locationError = locationError,
                photo = photo,
                onValueChangePhoto = {
                    photo = it
                    photoTouched = true
                },
                photoError = photoError,
                navigateToReportLocation = {
                    navigateToReportLocation()
                }
            )
        }

    }

}



