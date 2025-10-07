package com.hlasoftware.focus.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.home.data.repository.HomeRepository
import com.hlasoftware.focus.features.home.domain.repository.IHomeRepository
import com.hlasoftware.focus.features.home.domain.usecase.HomeUseCase
import com.hlasoftware.focus.features.home.presentation.HomeViewModel
import com.hlasoftware.focus.features.login.data.repository.LoginRepository
import com.hlasoftware.focus.features.login.domain.usecase.LoginUseCase
import com.hlasoftware.focus.features.login.presentation.LoginViewModel
import com.hlasoftware.focus.features.profile.application.ProfileViewModel
import com.hlasoftware.focus.features.profile.data.repository.ProfileRepository
import com.hlasoftware.focus.features.profile.domain.repository.IProfileRepository
import com.hlasoftware.focus.features.profile.domain.usecase.GetProfileUseCase
import com.hlasoftware.focus.features.signup.data.repository.SignUpRepositoryImpl
import com.hlasoftware.focus.features.signup.domain.repository.SignUpRepository
import com.hlasoftware.focus.features.signup.domain.usecase.SignUpUseCase
import com.hlasoftware.focus.features.signup.presentation.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { LoginRepository(get()) }
    factory { LoginUseCase(get()) }
    viewModel { LoginViewModel(get()) }


    single<IProfileRepository> { ProfileRepository() }
    factory { GetProfileUseCase(get()) }
    viewModel { ProfileViewModel(get()) }

    single <SignUpRepository>{ SignUpRepositoryImpl(get(),get()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    factory { SignUpUseCase(get()) }
    viewModel { SignUpViewModel(get()) }

    single<IHomeRepository> { HomeRepository() }
    factory { HomeUseCase(get()) }
    viewModel { HomeViewModel(get()) }
}