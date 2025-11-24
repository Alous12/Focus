package com.hlasoftware.focus.features.add_task.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Task(
    val id: String = "",
    val workgroupId: String = "",
    val name: String = "",
    val description: String? = null,
    val creatorId: String = "",
    val assignedMemberIds: List<String> = emptyList(),
    val dueDate: String? = null, // Using String for simplicity, consider Date or LocalDate
    val dueTime: String? = null, // Using String for simplicity, consider LocalTime
    @ServerTimestamp
    val createdAt: Date? = null,
)
