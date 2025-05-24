package com.mapify

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mapify.ui.navigation.Navigation
import com.mapify.ui.theme.MapifyTheme
import com.mapify.viewmodel.ConversationsViewModel
import com.mapify.viewmodel.MainViewModel
import com.mapify.viewmodel.ReportsViewModel
import com.mapify.viewmodel.UsersViewModel

class MainActivity : ComponentActivity() {

    private val usersViewModel: UsersViewModel by viewModels()
    private val reportsViewModel: ReportsViewModel by viewModels()
    private val conversationsViewModel: ConversationsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ConversationsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ConversationsViewModel(usersViewModel) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        val mainViewModel = MainViewModel(
            usersViewModel = usersViewModel,
            reportsViewModel = reportsViewModel,
            conversationsViewModel = conversationsViewModel
        )

        enableEdgeToEdge()
        setContent {
            MapifyTheme {
                Navigation(
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}