package com.hlasoftware.focus.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material.icons.outlined.GroupWork
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ViewTimeline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.time.LocalDate

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
    HOME(MainScreenTab.Home, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    WORK_GROUPS(MainScreenTab.WorkGroups, "Work Groups", Icons.Filled.GroupWork, Icons.Outlined.GroupWork),
    ROUTINES(MainScreenTab.Routines, "Rutinas", Icons.Filled.ViewTimeline, Icons.Outlined.ViewTimeline),
    PROFILE(MainScreenTab.Profile, "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun MainScreen(userId: String) {
    val navController = rememberNavController()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Estado para controlar la visibilidad del BottomSheet
    var showAddActivitySheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
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
        },
        floatingActionButton = {
            if (currentDestination?.route == MainScreenTab.Home.route) {
                FloatingActionButton(
                    onClick = { showAddActivitySheet = true }, // Muestra el BottomSheet
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir actividad",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainScreenTab.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainScreenTab.Home.route) { 
                HomeScreen(
                    userId = userId, 
                    selectedDate = selectedDate,
                    onDateChange = { newDate -> selectedDate = newDate },
                    showAddActivitySheet = showAddActivitySheet, // Pasa el estado
                    onDismissAddActivitySheet = { showAddActivitySheet = false } // Pasa la lambda para cerrarlo
                )
            }
            composable(MainScreenTab.Profile.route) { ProfileScreen(userId = userId) }
            // TODO: Add other composables for WorkGroups and Routines
        }
    }
}