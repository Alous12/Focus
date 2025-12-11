package com.hlasoftware.focus.features.routines.domain.usecase

import com.hlasoftware.focus.features.routines.domain.repository.RoutineRepository

class DeleteRoutineUseCase(private val repository: RoutineRepository) {
    suspend operator fun invoke(routineId: String) = repository.deleteRoutine(routineId)
}
