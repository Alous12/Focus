package com.hlasoftware.focus.features.home.domain.repository

import com.hlasoftware.focus.features.home.domain.model.HomeModel
import java.time.LocalDate

interface IHomeRepository {
    suspend fun getHomeData(userId: String, date: LocalDate): HomeModel
}