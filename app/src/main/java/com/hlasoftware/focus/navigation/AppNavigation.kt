package com.hlasoftware.focus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hlasoftware.focus.features.forgot_password.presentation.ForgotPasswordScreen
import com.hlasoftware.focus.features.login.presentation.LoginScreen
import com.hlasoftware.focus.features.signup.presentation.SignUpScreen
import org.koin.androidx.compose.koinViewModel

// Sealed class para las rutas de navegación principales de la app (antes del login)
sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login")
    object SignUp : AuthScreen("signup")
    object ForgotPassword : AuthScreen("forgot_password")
    object Main : AuthScreen("main/{userId}") { // Ruta que recibe el userId
        fun createRoute(userId: String) = "main/$userId"
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthScreen.Login.route
    ) {
        // Flujo de Autenticación
        composable(AuthScreen.Login.route) {
            LoginScreen(
                onLoginSuccess = { user -> // user es de tipo UserModel
                    navController.navigate(AuthScreen.Main.createRoute(user.userId)) {
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
                },
                onSignUpClicked = { navController.navigate(AuthScreen.SignUp.route) },
                onForgotPasswordClicked = { navController.navigate(AuthScreen.ForgotPassword.route) }
            )
        }

        composable(AuthScreen.SignUp.route) {
            SignUpScreen(
                viewModel = koinViewModel(), // CORREGIDO: Se pasa el ViewModel
                onSuccess = { user -> // user es de tipo ProfileModel
                    navController.navigate(AuthScreen.Main.createRoute(user.uid)) {
                        popUpTo(AuthScreen.SignUp.route) { inclusive = true }
                    }
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(AuthScreen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }

        // Flujo Principal (después del login)
        composable(
            route = AuthScreen.Main.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            MainScreen(
                userId = userId,
                onLogout = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
