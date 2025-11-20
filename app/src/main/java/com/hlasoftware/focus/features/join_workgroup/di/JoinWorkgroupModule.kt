package com.hlasoftware.focus.features.join_workgroup.di

import com.hlasoftware.focus.features.join_workgroup.data.repository.JoinWorkgroupRepositoryImpl
import com.hlasoftware.focus.features.join_workgroup.domain.repository.JoinWorkgroupRepository
import com.hlasoftware.focus.features.join_workgroup.domain.usecase.JoinWorkgroupUseCase
import com.hlasoftware.focus.features.join_workgroup.presentation.JoinWorkgroupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val joinWorkgroupModule = module {
    single<JoinWorkgroupRepository> { JoinWorkgroupRepositoryImpl(get()) }
    factory { JoinWorkgroupUseCase(get()) }
    viewModel { JoinWorkgroupViewModel(get()) }
}
