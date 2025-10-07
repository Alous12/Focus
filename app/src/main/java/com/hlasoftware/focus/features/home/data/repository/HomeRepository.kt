package com.hlasoftware.focus.features.home.data.repository

import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import com.hlasoftware.focus.features.home.domain.model.ActivityType
import com.hlasoftware.focus.features.home.domain.model.HomeModel
import com.hlasoftware.focus.features.home.domain.repository.IHomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class HomeRepository : IHomeRepository {

    override suspend fun getHomeData(userId: String): HomeModel {
        delay(500)

        val activities = listOf(
            ActivityModel("1", "Clase de Matemáticas", "08:00 - 09:30", ActivityType.CLASS),
            ActivityModel("2", "Entrega de Proyecto", "10:00 - 11:00", ActivityType.TASK),
            ActivityModel("3", "Reunión con Tutor", "12:00 - 12:30", ActivityType.MEETING)
        )

        return HomeModel(upcomingActivities = activities)
    }
}
