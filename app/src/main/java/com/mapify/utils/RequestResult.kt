package com.mapify.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay

sealed class RequestResult {
    data class Success(val message: String) : RequestResult()
    data class Failure(val message: String) : RequestResult()
    object Loading : RequestResult()
}

@Composable
fun RequestResultEffectHandler(
    requestResult: RequestResult?,
    context: Context,
    isLoading: MutableState<Boolean>,
    isDeleting: MutableState<Boolean> = mutableStateOf(false),
    onResetResult: () -> Unit,
    onNavigate: () -> Unit = {},
    isReportViewScreen: Boolean = false
) {
    LaunchedEffect(requestResult) {
        when (requestResult) {
            null -> {
                isLoading.value = false
            }
            is RequestResult.Success -> {
                isLoading.value = false
                Toast.makeText(context, requestResult.message, Toast.LENGTH_SHORT).show()
                delay(600)
                onResetResult()
                if (isDeleting.value) {
                    isDeleting.value = false
                    onNavigate()
                }
                if (!isReportViewScreen) {
                    onNavigate()
                }
            }
            is RequestResult.Failure -> {
                isLoading.value = false
                Toast.makeText(context, requestResult.message, Toast.LENGTH_SHORT).show()
                delay(600)
                onResetResult()
            }
            is RequestResult.Loading -> {
                isLoading.value = true
            }
        }
    }
}