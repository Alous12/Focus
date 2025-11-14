package com.hlasoftware.focus.features.routines.domain.usecase

import com.hlasoftware.focus.features.routines.domain.repository.RoutineRepository

class GetRoutinesUseCase(private val repository: RoutineRepository) {
    operator fun invoke() = repository.getRoutines()
}
