package com.hlasoftware.focus.features.add_task.domain.repository

import com.hlasoftware.focus.features.add_task.domain.model.Task

interface AddTaskRepository {
    suspend fun addTask(task: Task): Result<Unit>
}
