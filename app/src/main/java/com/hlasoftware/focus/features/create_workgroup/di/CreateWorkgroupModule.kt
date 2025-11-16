package com.hlasoftware.focus.features.create_workgroup.di

import com.hlasoftware.focus.features.create_workgroup.data.repository.CreateWorkgroupRepositoryImpl
import com.hlasoftware.focus.features.create_workgroup.domain.repository.CreateWorkgroupRepository
import com.hlasoftware.focus.features.create_workgroup.domain.usecase.CreateWorkgroupUseCase
import com.hlasoftware.focus.features.create_workgroup.presentation.CreateWorkgroupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val createWorkgroupModule = module {
    single<CreateWorkgroupRepository> { CreateWorkgroupRepositoryImpl(get(), get()) }
    factory { CreateWorkgroupUseCase(get(), get()) } // Provides CreateWorkgroupRepository and GetProfileUseCase
    viewModel { CreateWorkgroupViewModel(get()) }
}
