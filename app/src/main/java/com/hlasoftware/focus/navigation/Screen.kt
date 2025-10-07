package com.hlasoftware.focus.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object CardExamples: Screen("card")
    object SignUp: Screen("signup")
    object Profile: Screen("profile")
    object Github: Screen("github")
}