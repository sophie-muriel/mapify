package com.mapify.model

data class ReportFilters(
    val onlyPriority: Boolean = false,
    val onlyResolved: Boolean = false,
    val onlyVerified: Boolean = false,
    val onlyMyPosts: Boolean = false
)
