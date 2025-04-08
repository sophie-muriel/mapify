package com.mapify.ui.users.tabs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeTab(
    isAdmin: Boolean
){
    if (isAdmin) {
        Text("Welcome, Admin! (｀・ω・´)ゞ")
    } else {
        Text("Welcome, User! (・∀・)")
    }
}