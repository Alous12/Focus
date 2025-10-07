package com.hlasoftware.focus.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hlasoftware.focus.features.forgot_password.presentation.ForgotPasswordScreen
import com.hlasoftware.focus.features.home.presentation.HomeScreen
import com.hlasoftware.focus.features.login.presentation.LoginScreen
import com.hlasoftware.focus.features.profile.application.ProfileScreen
import com.hlasoftware.focus.features.signup.presentation.SignUpScreen
import com.hlasoftware.focus.features.signup.presentation.SignUpViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(
            route = "${Screen.Home.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            HomeScreen(userId = userId)
        }

        // Login
        composable(Screen.Login.route) {
            LoginScreen(
                onForgotPasswordClicked = { navController.navigate(Screen.ForgotPassword.route) },
                onLoginSuccess = { user ->
                    navController.navigate("${Screen.Home.route}/${user.userId}") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignUpClicked = { navController.navigate(Screen.SignUp.route) }
            )
        }


        composable(Screen.SignUp.route) {
            val signUpViewModel: SignUpViewModel = koinViewModel()

            SignUpScreen(
                viewModel = signUpViewModel,
                onBackClicked = {
                    navController.popBackStack()
                },
                onSuccess = { userProfile ->
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }



        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }


        // Home
        composable(
            route = "${Screen.Home.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            HomeScreen(userId = userId)
        }

        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}
