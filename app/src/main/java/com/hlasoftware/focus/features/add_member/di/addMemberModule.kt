package com.hlasoftware.focus.features.add_member.di

import com.hlasoftware.focus.features.add_member.data.datasource.AddMemberFirestoreDataSource
import com.hlasoftware.focus.features.add_member.data.datasource.IAddMemberDataSource
import com.hlasoftware.focus.features.add_member.data.repository.AddMemberRepositoryImpl
import com.hlasoftware.focus.features.add_member.domain.repository.IAddMemberRepository
import com.hlasoftware.focus.features.add_member.domain.usecase.AddMemberUseCase
import com.hlasoftware.focus.features.add_member.domain.usecase.SearchUsersUseCase
import com.hlasoftware.focus.features.add_member.presentation.AddMemberViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val addMemberModule = module {
    viewModel { AddMemberViewModel(get(), get(), get()) }
    factory { SearchUsersUseCase(get()) }
    factory { AddMemberUseCase(get()) }
    single<IAddMemberRepository> { AddMemberRepositoryImpl(get()) }
    single<IAddMemberDataSource> { AddMemberFirestoreDataSource(get()) }
}
