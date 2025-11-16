package com.hlasoftware.focus.features.routines.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class Routine(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val days: List<String> = emptyList(),
    val startTime: String? = null,
    val endTime: String? = null,
    val color: Any = Color.White.toArgb(), // Can be Int (new) or String (old)
    val userId: String = ""
)
