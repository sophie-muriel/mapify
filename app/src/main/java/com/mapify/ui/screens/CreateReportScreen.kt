package com.mapify.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(){

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Create Report")
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
                }
            )

        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {  }

    }

}

