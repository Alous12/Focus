package com.hlasoftware.focus.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.activities.data.repository.ActivityRepositoryImpl
import com.hlasoftware.focus.features.add_routines.domain.usecase.AddRoutineUseCase
import com.hlasoftware.focus.features.add_routines.presentation.AddRoutineViewModel
import com.hlasoftware.focus.features.create_activity.presentation.CreateActivityViewModel
import com.hlasoftware.focus.features.home.domain.usecase.HomeUseCase
import com.hlasoftware.focus.features.home.presentation.HomeViewModel
import com.hlasoftware.focus.features.login.data.repository.LoginRepository
import com.hlasoftware.focus.features.login.domain.usecase.LoginUseCase
import com.hlasoftware.focus.features.login.presentation.LoginViewModel
import com.hlasoftware.focus.features.posts.data.repository.PostRepositoryImpl
import com.hlasoftware.focus.features.posts.domain.repository.IPostRepository
import com.hlasoftware.focus.features.posts.domain.usecase.CreatePostUseCase
import com.hlasoftware.focus.features.posts.domain.usecase.DeletePostUseCase
import com.hlasoftware.focus.features.posts.domain.usecase.GetPostsUseCase
import com.hlasoftware.focus.features.posts.domain.usecase.UpdatePostUseCase
import com.hlasoftware.focus.features.profile.application.ProfileViewModel
import com.hlasoftware.focus.features.profile.data.repository.ProfileRepository
import com.hlasoftware.focus.features.profile.domain.repository.IProfileRepository
import com.hlasoftware.focus.features.profile.domain.usecase.GetProfileUseCase
import com.hlasoftware.focus.features.routines.data.repository.RoutineRepositoryImpl
import com.hlasoftware.focus.features.routines.domain.repository.RoutineRepository
import com.hlasoftware.focus.features.routines.domain.usecase.GetRoutinesUseCase
import com.hlasoftware.focus.features.routines.presentation.RoutinesViewModel
import com.hlasoftware.focus.features.profile.domain.usecase.UpdateProfileUseCase
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

    single<IProfileRepository> { ProfileRepository(
        firestore = get(),
        auth = get(),
        storage = get()
    )}
    factory { GetProfileUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get(), get(), get()) } 

    single<SignUpRepository> { SignUpRepositoryImpl(get(), get()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
    factory { SignUpUseCase(get()) }
    viewModel { SignUpViewModel(get()) }

    single<ActivityRepository> { ActivityRepositoryImpl(get()) }
    factory { HomeUseCase(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { CreateActivityViewModel(get(), get()) }

    // Routines
    single<RoutineRepository> { RoutineRepositoryImpl(get(), get()) }
    factory { GetRoutinesUseCase(get()) }
    viewModel { RoutinesViewModel(get()) }

    // Add Routine
    factory { AddRoutineUseCase(get()) }
    viewModel { AddRoutineViewModel(get()) }

    single<IPostRepository> { PostRepositoryImpl(get(), get()) }
    factory { GetPostsUseCase(get()) }
    factory { CreatePostUseCase(get()) }
    factory { UpdatePostUseCase(get()) }
    factory { DeletePostUseCase(get()) }
}
