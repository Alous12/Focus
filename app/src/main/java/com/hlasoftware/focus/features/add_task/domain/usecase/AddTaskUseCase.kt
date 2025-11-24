package com.hlasoftware.focus.features.add_task.domain.usecase

import com.hlasoftware.focus.features.add_task.domain.model.Task
import com.hlasoftware.focus.features.add_task.domain.repository.AddTaskRepository

class AddTaskUseCase(private val repository: AddTaskRepository) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        return repository.addTask(task)
    }
}
