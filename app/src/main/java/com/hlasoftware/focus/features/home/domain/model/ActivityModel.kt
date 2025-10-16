package com.hlasoftware.focus.features.home.domain.model

import java.time.LocalDate

enum class ActivityType {
    CLASS, TASK, MEETING
}

data class ActivityModel(
    val id: String,
    val title: String,
    val timeRange: String,
    val type: ActivityType,
    val date: LocalDate
)
