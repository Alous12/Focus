package com.hlasoftware.focus.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import com.hlasoftware.focus.features.home.domain.model.ActivityType
import com.hlasoftware.focus.ui.theme.IndicatorClass
import com.hlasoftware.focus.ui.theme.IndicatorMeeting
import com.hlasoftware.focus.ui.theme.IndicatorTask
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: String,
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    var selectedBottomNavItem by remember { mutableStateOf(BottomNavItem.Home) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Este efecto se ejecutará cada vez que `selectedDate` o `userId` cambien.
    LaunchedEffect(userId, selectedDate) {
        homeViewModel.loadHome(userId, selectedDate)
    }

    Scaffold(
        topBar = { HomeTopAppBar() },
        bottomBar = {
            HomeBottomNavigationBar(
                selectedItem = selectedBottomNavItem,
                onItemSelected = { selectedBottomNavItem = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Handle FAB click */ },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Actividad")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            DateSelector(
                currentDate = selectedDate,
                onPreviousDay = { selectedDate = selectedDate.minusDays(1) },
                onNextDay = { selectedDate = selectedDate.plusDays(1) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is HomeUiState.Success -> {
                    val activities = (uiState as HomeUiState.Success).activities
                    if (activities.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay actividades para este día.", color = MaterialTheme.colorScheme.onBackground)
                        }
                    } else {
                        ActivitiesList(
                            activities = activities,
                            onOptionsClicked = { activityId ->
                                homeViewModel.onActivityOptionsClicked(activityId)
                            }
                        )
                    }
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: ${(uiState as HomeUiState.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateSelector(
    currentDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    val formattedDate = currentDate.format(formatter).replaceFirstChar { it.titlecase(Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(50))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(Icons.Default.ArrowBackIos, contentDescription = "Día anterior", tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Text(formattedDate, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
        IconButton(onClick = onNextDay) {
            Icon(Icons.Default.ArrowForwardIos, contentDescription = "Día siguiente", tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
fun ActivitiesList(
    activities: List<ActivityModel>,
    onOptionsClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(activities, key = { it.id }) { activity ->
            ActivityCard(
                activity = activity,
                onOptionsClicked = { onOptionsClicked(activity.id) }
            )
        }
    }
}

@Composable
fun ActivityCard(activity: ActivityModel, onOptionsClicked: () -> Unit) {
    val indicatorColor = when (activity.type) {
        ActivityType.CLASS -> IndicatorClass
        ActivityType.TASK -> IndicatorTask
        ActivityType.MEETING -> IndicatorMeeting
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = activity.timeRange,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onOptionsClicked) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Opciones",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Bottom Navigation
enum class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Home("home_screen_route", "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    WorkGroups("work_groups_route", "Work Groups", Icons.Filled.GroupWork, Icons.Outlined.GroupWork),
    Routines("routines_route", "Rutinas", Icons.Filled.ViewTimeline, Icons.Outlined.ViewTimeline),
    Profile("profile_route", "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun HomeBottomNavigationBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit
) {
    val items = BottomNavItem.values()
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selectedItem == item) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, fontSize = 10.sp, maxLines = 1) },
                selected = selectedItem == item,
                onClick = { onItemSelected(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "Actividades Próximas",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}
