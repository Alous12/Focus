package com.hlasoftware.focus.features.home.domain.model

enum class ActivityType {
    CLASS, TASK, MEETING
}

data class ActivityModel(
    val id: String,
    val title: String,
    val timeRange: String,
    val type: ActivityType
)