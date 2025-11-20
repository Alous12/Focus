package com.hlasoftware.focus.features.workgroup_details.di

import com.hlasoftware.focus.features.workgroup_details.data.repository.WorkgroupDetailsRepositoryImpl
import com.hlasoftware.focus.features.workgroup_details.domain.repository.WorkgroupDetailsRepository
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.DeleteWorkgroupUseCase
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.GetWorkgroupDetailsUseCase
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.LeaveWorkgroupUseCase
import com.hlasoftware.focus.features.workgroup_details.presentation.WorkgroupDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val workgroupDetailsModule = module {
    single<WorkgroupDetailsRepository> { WorkgroupDetailsRepositoryImpl(get()) }
    factory { GetWorkgroupDetailsUseCase(get()) }
    factory { DeleteWorkgroupUseCase(get()) }
    factory { LeaveWorkgroupUseCase(get()) } 
    viewModel { WorkgroupDetailsViewModel(get(), get(), get()) }
}
