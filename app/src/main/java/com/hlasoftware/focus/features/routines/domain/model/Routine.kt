package com.hlasoftware.focus.features.routines.domain.model

import androidx.compose.ui.graphics.Color

data class Routine(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val days: List<String> = emptyList(),
    val startTime: String? = null,
    val endTime: String? = null,
    val color: String = "#FFFFFF", // Storing color as a hex string
    val userId: String = ""
)
