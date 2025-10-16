package com.hlasoftware.focus.features.home.data.repository

import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import com.hlasoftware.focus.features.home.domain.model.ActivityType
import com.hlasoftware.focus.features.home.domain.model.HomeModel
import com.hlasoftware.focus.features.home.domain.repository.IHomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate

class HomeRepository : IHomeRepository {

    // Datos de ejemplo con fechas
    private val mockActivities = listOf(
        // Hoy
        ActivityModel("1", "Clase de Física", "14:00 - 15:00", ActivityType.CLASS, LocalDate.now()),
        ActivityModel("2", "Tarea de Matemáticas", "14:00 - 15:00", ActivityType.TASK, LocalDate.now()),
        ActivityModel("3", "Reunión Grupal", "14:00 - 15:00", ActivityType.MEETING, LocalDate.now()),

        // Mañana
        ActivityModel("4", "Examen de Cálculo", "09:00 - 11:00", ActivityType.CLASS, LocalDate.now().plusDays(1)),
        ActivityModel("5", "Llamada con el equipo", "16:00 - 16:30", ActivityType.MEETING, LocalDate.now().plusDays(1)),

        // Ayer
        ActivityModel("6", "Entrega de Ensayo", "Todo el día", ActivityType.TASK, LocalDate.now().minusDays(1))
    )

    override suspend fun getHomeData(userId: String, date: LocalDate): HomeModel {
        return withContext(Dispatchers.IO) {
            delay(500) // Simular latencia de red

            // Filtrar actividades por la fecha seleccionada
            val filteredActivities = mockActivities.filter { it.date == date }

            HomeModel(upcomingActivities = filteredActivities)
        }
    }
}
