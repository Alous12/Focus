package com.hlasoftware.focus.features.home.domain.repository

import com.hlasoftware.focus.features.home.domain.model.HomeModel

interface IHomeRepository {
    suspend fun getHomeData(userId: String): HomeModel
}