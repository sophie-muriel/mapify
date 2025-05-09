package com.mapify

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.mapify.ui.navigation.Navigation
import com.mapify.ui.theme.MapifyTheme
import com.mapify.viewmodel.MainViewModel
import com.mapify.viewmodel.ReportsViewModel
import com.mapify.viewmodel.UsersViewModel

class MainActivity : ComponentActivity() {

    private val usersViewModel: UsersViewModel by viewModels()
    private val reportsViewModel: ReportsViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        val mainViewModel = MainViewModel(
            usersViewModel = usersViewModel,
            reportsViewModel = reportsViewModel
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