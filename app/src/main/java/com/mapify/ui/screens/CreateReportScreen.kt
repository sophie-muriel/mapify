package com.mapify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mapify.R
import com.mapify.ui.components.ReportForm
import com.mapify.ui.theme.Spacing
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import com.mapify.model.Category
import com.mapify.model.Location
import com.mapify.model.Role
import com.mapify.model.User
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    navigateToHome: () -> Unit,
    navigateToReportLocation: () -> Unit,
    navigateToReportView: () -> Unit
) {

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

    val regex = Regex("^(https?:\\/\\/)?([a-zA-Z0-9.-]+)\\.([a-zA-Z]{2,})(\\/\\S*)?$")
    var photo by rememberSaveable { mutableStateOf("") }
    var photoTouched by rememberSaveable { mutableStateOf(false) }
    val photoError = photoTouched && !regex.matches(photo)

    val reportsList = remember { mutableStateListOf<Report>() }
    val embeddedUser = User(
        id = "1",
        fullName = "Embedded User",
        email = "embedded@mail.com",
        password = "ThisIsATestPass",
        role = Role.CLIENT,
        registrationLocation = Location(
            latitude = 43230.1, longitude = 753948.7, country = "Colombia", city = "Armenia"
        )
    )
    var reportsIdCounter by rememberSaveable { mutableIntStateOf(1) }

    val context = LocalContext.current

    val isKeyboardActive = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }
    var publishReportVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (!isKeyboardActive) {
                TopAppBar(modifier = Modifier.padding(horizontal = Spacing.Small), title = {
                    Text(
                        text = stringResource(id = R.string.create_report),
                        style = MaterialTheme.typography.titleLarge
                    )
                }, navigationIcon = {
                    IconButton(
                        onClick = {
                            exitDialogVisible = true
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_arrow_icon)
                        )
                    }
                })
            }
        }) { padding ->

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
                placeHolder = stringResource(id = R.string.category),
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
                },
                onClickCreate = {
                    publishReportVisible = true
                })
        }

    }
    if (exitDialogVisible) {
        ExitReportCreationDialog(onClose = {
            exitDialogVisible = false
        }, onExit = {
            exitDialogVisible = false
            navigateToHome()
        })
    }

    if (publishReportVisible) {
        PublishReportDialog(onClose = {
            publishReportVisible = false
        }, onPublish = {
            publishReportVisible = false
            val newReport = Report(
                title = title,
                category = Category.entries.find { it.displayName == dropDownValue }!!,
                description = description,
                location = null, //TODO: This must be changed here and in Report model erase the "?"
                images = listOf(photo),
                id = reportsIdCounter.toString(),
                status = ReportStatus.NOT_VERIFIED,
                userId = embeddedUser.id,
                date = LocalDateTime.now()
            )
            reportsIdCounter++
            reportsList.add(newReport)
            navigateToReportView()
        })
    }
}

@Composable
fun ExitReportCreationDialog(
    onClose: () -> Unit, onExit: () -> Unit
) {
    Dialog(
        onDismissRequest = { onClose() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Spacer(modifier = Modifier.height(Spacing.Sides))

            Text(
                text = stringResource(id = R.string.exit_report_creation),
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(
                    horizontal = Spacing.Sides, vertical = Spacing.Small
                ),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(id = R.string.exit_report_creation_description),
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(
                    horizontal = Spacing.Sides, vertical = Spacing.Small
                ),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(Spacing.Small))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Spacing.Sides
                    ), horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        onClose()
                    }) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    modifier = Modifier
                        .wrapContentSize()
                        .height(40.dp),
                    onClick = {
                        onExit()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                ) {
                    Text(
                        text = stringResource(id = R.string.exit),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.Sides))
        }
    }
}

@Composable
fun PublishReportDialog(
    onClose: () -> Unit, onPublish: () -> Unit
) {
    Dialog(
        onDismissRequest = { onClose() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Spacer(modifier = Modifier.height(Spacing.Sides))

            Text(
                text = stringResource(id = R.string.publish_report),
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(
                    horizontal = Spacing.Sides, vertical = Spacing.Small
                ),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(id = R.string.publish_report_description),
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(
                    horizontal = Spacing.Sides, vertical = Spacing.Small
                ),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(Spacing.Small))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Spacing.Sides
                    ), horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        onClose()
                    }) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    modifier = Modifier
                        .wrapContentSize()
                        .height(40.dp),
                    onClick = {
                        onPublish()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                ) {
                    Text(
                        text = stringResource(id = R.string.publish),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.Sides))
        }
    }
}


