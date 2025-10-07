package com.hlasoftware.focus.features.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*    // <-- Añade esta línea
import androidx.compose.material.icons.outlined.*
// import androidx.compose.material.icons.filled.ArrowBackIosNew // Ya no se necesita
// import androidx.compose.material.icons.filled.ArrowForwardIos // Ya no se necesita
 // Reemplaza con tus íconos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
// import androidx.compose.ui.graphics.PathEffect // Para borde punteado, opcional
// import androidx.compose.ui.graphics.drawscope.Stroke // Para borde punteado, opcional
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hlasoftware.focus.features.home.domain.model.ActivityModel // Asegúrate que la ruta es correcta
import com.hlasoftware.focus.features.home.domain.model.ActivityType // Asegúrate que la ruta es correcta
import org.koin.androidx.compose.koinViewModel
import com.hlasoftware.focus.features.home.presentation.HomeViewModel

// Define tus colores (puedes moverlos a un archivo Theme.kt si los usas en más sitios)
val CelestePrincipal = Color(0xFF03A9F4)
val CelesteClaroIndicador = Color(0xFFB3E5FC)
val ColorBordeTarjeta = Color(0xFF0277BD)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: String,
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    var selectedBottomNavItem by remember { mutableStateOf(BottomNavItem.Home) }

    // Carga actividades al montar la pantalla
    LaunchedEffect(userId) {
        homeViewModel.loadHome(userId)
    }

    Scaffold(
        topBar = { HomeTopAppBar() },
        bottomBar = {
            HomeBottomNavigationBar(
                selectedItem = selectedBottomNavItem,
                onItemSelected = { selectedBottomNavItem = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when (uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CelestePrincipal)
                    }
                }
                is HomeUiState.Success -> {
                    val activities = (uiState as HomeUiState.Success).activities
                    Spacer(modifier = Modifier.height(16.dp))
                    if (activities.isEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("No hay actividades próximas.", color = Color.White)
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
fun ActivitiesList(
    activities: List<ActivityModel>,
    onOptionsClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
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
        ActivityType.CLASS -> Color(0xFFD32F2F)
        ActivityType.TASK -> Color(0xFF388E3C)
        ActivityType.MEETING -> Color(0xFF1976D2)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, ColorBordeTarjeta.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Hora: ${activity.timeRange}",
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
    WorkGroups("work_groups_route", "Work Groups", Icons.Filled.Settings, Icons.Outlined.Settings),
    Routines("routines_route", "Rutinas", Icons.Filled.Settings, Icons.Outlined.Settings),
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
                    selectedIconColor = CelestePrincipal,
                    selectedTextColor = CelestePrincipal,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = CelestePrincipal.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar() {
    Column {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        TopAppBar(
            title = {
                Text(
                    text = "Actividades Próximas",
                    color = CelestePrincipal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}