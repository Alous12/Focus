package com.hlasoftware.focus.features.workgroups.di

import com.hlasoftware.focus.features.workgroups.data.repository.WorkgroupRepositoryImpl
import com.hlasoftware.focus.features.workgroups.domain.repository.WorkgroupRepository
import com.hlasoftware.focus.features.workgroups.domain.usecase.GetWorkgroupsUseCase
import com.hlasoftware.focus.features.workgroups.presentation.WorkgroupsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val workgroupsModule = module {
    single<WorkgroupRepository> { WorkgroupRepositoryImpl(get()) }
    factory { GetWorkgroupsUseCase(get()) }
    viewModel { WorkgroupsViewModel(get()) }
}
