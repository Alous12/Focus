package com.hlasoftware.focus.features.home.domain.model

import com.google.firebase.firestore.DocumentId

data class ActivityModel(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val startTime: String? = null,
    val endTime: String? = null,
    val type: String = "TASK"
)
