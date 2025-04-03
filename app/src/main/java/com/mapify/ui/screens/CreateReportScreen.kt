package com.mapify.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mapify.ui.components.GenericDropDownMenu
import com.mapify.ui.components.GenericTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(){

    var title by rememberSaveable { mutableStateOf("") }
    var titleTouched by rememberSaveable { mutableStateOf(false) }
    val titleError = titleTouched && title.isBlank()

    var dropDownValue by rememberSaveable { mutableStateOf("") }
    var dropDownExpanded by rememberSaveable { mutableStateOf(false) }
    var dropDownTouched by rememberSaveable { mutableStateOf(false) }
    val categories = listOf("Security", "Medical Emergency", "Infrastructure", "Pets", "Community")
    val dropDownError = dropDownValue.isBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Report",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {  }
                    )  {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Black Arrow"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {  }
                    )  {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Check"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ReportCreationForm(
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
                isTouched = dropDownTouched
            )
        }

    }

}

@Composable
fun ReportCreationForm(
    title: String,
    onValueChangeTitle: (String) -> Unit,
    titleError: Boolean,
    placeHolder: String,
    value: String,
    onValueChange: (String) -> Unit,
    items: List<String>,
    dropDownError: Boolean,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    isTouched: Boolean,
    onDismissRequest: () -> Unit
    //password: String,
    //onValueChangePassword: (String) -> Unit,
    //passwordError: Boolean,
    //passwordConfirmation: String,
    //onValueChangePasswordConfirmation: (String) -> Unit,
    //passwordConfirmationError: Boolean,
    //onClickRegister: () -> Unit,
    //navigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GenericTextField(
            value = title,
            supportingText = "You must provide a title",
            label = "Title",
            onValueChange = onValueChangeTitle,
            isError = titleError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        GenericDropDownMenu(
            placeholder = placeHolder,
            value = value,
            onValueChange = onValueChange,
            items = items,
            isError = dropDownError,
            supportingText = "Select a City",
            isExpanded = isExpanded,
            onExpandedChange = onExpandedChange,
            onDismissRequest = onDismissRequest,
            isTouched = isTouched
        )


    }
}

