package com.hlasoftware.focus.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material.icons.outlined.GroupWork
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ViewTimeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hlasoftware.focus.features.home.presentation.HomeScreen
import com.hlasoftware.focus.features.profile.application.ProfileScreen

// CORREGIDO: Renombrado de 'Screen' a 'MainScreenTab' para evitar conflicto de nombres
sealed class MainScreenTab(val route: String) {
    object Home : MainScreenTab("home")
    object Profile : MainScreenTab("profile")
    object WorkGroups : MainScreenTab("work_groups")
    object Routines : MainScreenTab("routines")
}

enum class BottomNavItem(
    val screen: MainScreenTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    // CORREGIDO: Actualizado para usar MainScreenTab
    HOME(MainScreenTab.Home, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    WORK_GROUPS(MainScreenTab.WorkGroups, "Work Groups", Icons.Filled.GroupWork, Icons.Outlined.GroupWork),
    ROUTINES(MainScreenTab.Routines, "Rutinas", Icons.Filled.ViewTimeline, Icons.Outlined.ViewTimeline),
    PROFILE(MainScreenTab.Profile, "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun MainScreen(userId: String) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            NavigationBar {
                BottomNavItem.entries.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (currentDestination?.hierarchy?.any { it.route == item.screen.route } == true) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainScreenTab.Home.route, // CORREGIDO
            modifier = Modifier.padding(innerPadding)
        ) {
            // CORREGIDO
            composable(MainScreenTab.Home.route) { HomeScreen(userId = userId) }
            composable(MainScreenTab.Profile.route) { ProfileScreen(userId = userId) }
            // TODO: Add other composables for WorkGroups and Routines
        }
    }
}
