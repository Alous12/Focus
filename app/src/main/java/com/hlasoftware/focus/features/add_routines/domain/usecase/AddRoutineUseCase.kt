package com.hlasoftware.focus.features.add_routines.domain.usecase

import com.hlasoftware.focus.features.routines.domain.model.Routine
import com.hlasoftware.focus.features.routines.domain.repository.RoutineRepository

class AddRoutineUseCase(private val repository: RoutineRepository) {
    suspend operator fun invoke(routine: Routine) = repository.addRoutine(routine)
}
