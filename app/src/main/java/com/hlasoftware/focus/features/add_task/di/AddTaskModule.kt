package com.hlasoftware.focus.features.add_task.di

import com.hlasoftware.focus.features.add_task.data.repository.AddTaskRepositoryImpl
import com.hlasoftware.focus.features.add_task.domain.repository.AddTaskRepository
import com.hlasoftware.focus.features.add_task.domain.usecase.AddTaskUseCase
import com.hlasoftware.focus.features.add_task.presentation.AddTaskViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val addTaskModule = module {
    single<AddTaskRepository> { AddTaskRepositoryImpl(get()) }
    factory { AddTaskUseCase(get()) }
    viewModel { AddTaskViewModel(get()) }
}
