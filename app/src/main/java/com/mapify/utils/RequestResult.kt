package com.mapify.utils

import android.content.Context
import android.util.Log
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
                //Log.d("ReportResult", "Result is null")
                isLoading.value = false
            }
            is RequestResult.Success -> {
                //Log.d("ReportResult", "Success result detected")
                isLoading.value = false
                Toast.makeText(context, requestResult.message, Toast.LENGTH_SHORT).show()
                delay(1000)
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
                //Log.d("ReportResult", "Failure result detected")
                isLoading.value = false
                Toast.makeText(context, requestResult.message, Toast.LENGTH_SHORT).show()
                delay(1000)
                onResetResult()
            }
            is RequestResult.Loading -> {
                //Log.d("ReportResult", "Loading result detected")
                isLoading.value = true
            }
        }
    }
}