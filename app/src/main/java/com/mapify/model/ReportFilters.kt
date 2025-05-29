package com.mapify.model

data class ReportFilters(
    val onlyPriority: Boolean = false,
    val onlyResolved: Boolean = false,
    val onlyVerified: Boolean = false,
    val onlyMyPosts: Boolean = false,
    val onlyThisDate: Boolean = false,
    val onlyThisDistance: Boolean = false,
    val thisDate: String = "",
    val thisDistance: Double = 0.0,
    val areSet: Boolean = onlyPriority || onlyResolved || onlyVerified || onlyMyPosts || onlyThisDate || onlyThisDistance || thisDate.isNotBlank() || thisDistance != 0.0,
    val isJustDistance: Boolean = onlyThisDistance && thisDistance != 0.0 && !onlyPriority && !onlyResolved && !onlyVerified && !onlyMyPosts && !onlyThisDate
)
