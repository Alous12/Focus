package com.hlasoftware.focus.features.routines.domain.repository

import com.hlasoftware.focus.features.routines.domain.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
    fun getRoutines(): Flow<List<Routine>>
    suspend fun addRoutine(routine: Routine): Result<Unit>
    suspend fun deleteRoutine(routineId: String): Result<Unit>
}
