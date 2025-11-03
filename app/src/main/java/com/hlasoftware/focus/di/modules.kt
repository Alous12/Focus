package com.hlasoftware.focus.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.activities.data.repository.ActivityRepositoryImpl
import com.hlasoftware.focus.features.create_activity.presentation.CreateActivityViewModel
import com.hlasoftware.focus.features.home.data.repository.HomeRepositoryImpl
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

    // --- 1. DEPENDENCIAS DE FIREBASE (Definidas primero para claridad) ---
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // --- 2. FEATURE LOGIN ---
    // LoginRepository solo necesita FirebaseAuth (get())
    single { LoginRepository(get()) }
    factory { LoginUseCase(get()) }
    viewModel { LoginViewModel(get()) }


    // --- 3. FEATURE PROFILE (CORREGIDO) ---
    single<IProfileRepository> {
        ProfileRepository(
            firestore = get(), // ⬅️ Koin inyecta FirebaseFirestore
            auth = get()       // ⬅️ Koin inyecta FirebaseAuth
        )
    }
    factory { GetProfileUseCase(get()) }
    viewModel { ProfileViewModel(get()) }

    // --- 4. FEATURE SIGNUP ---
    // SignUpRepositoryImpl requiere FirebaseAuth y FirebaseFirestore (ambos get())
    single <SignUpRepository>{ SignUpRepositoryImpl(get(), get()) }
    factory { SignUpUseCase(get()) }
    viewModel { SignUpViewModel(get()) }

    // --- 5. ACTIVITY/HOME ---
    single<ActivityRepository> { ActivityRepositoryImpl(get()) }
    single<IHomeRepository> { HomeRepositoryImpl(get()) }
    factory { HomeUseCase(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { CreateActivityViewModel(get(), get()) }
}