package com.hlasoftware.focus.features.activity_details.di

import com.hlasoftware.focus.features.activity_details.data.repository.ActivityDetailsRepositoryImpl
import com.hlasoftware.focus.features.activity_details.domain.repository.ActivityDetailsRepository
import com.hlasoftware.focus.features.activity_details.domain.usecase.GetActivityDetailsUseCase
import com.hlasoftware.focus.features.activity_details.presentation.ActivityDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityDetailsModule = module {
    single<ActivityDetailsRepository> { ActivityDetailsRepositoryImpl(get()) }
    factory { GetActivityDetailsUseCase(get()) }
    viewModel { ActivityDetailsViewModel(get()) }
}
