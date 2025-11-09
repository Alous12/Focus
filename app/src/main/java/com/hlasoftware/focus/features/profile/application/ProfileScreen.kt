package com.hlasoftware.focus.features.profile.application

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

enum class ProfileTab(val title: String) {
    INFO("Información"),
    POSTS("Posts"),
    CALENDAR("Calendario")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String, // userId recibido como parámetro
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val state by profileViewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(ProfileTab.INFO) }

    // Cargar el perfil cuando el userId esté disponible
    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            profileViewModel.showProfile(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val currentState = state) {
            is ProfileViewModel.ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileViewModel.ProfileUiState.Success -> {
                ProfileHeader(
                    profile = currentState.profile,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        ProfileTab.INFO -> InfoContent(profile = currentState.profile)
                        ProfileTab.POSTS -> PostsContent()
                        ProfileTab.CALENDAR -> CalendarContent()
                    }
                }
            }
            is ProfileViewModel.ProfileUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(currentState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
            }
            ProfileViewModel.ProfileUiState.Init -> {
                // Opcional: Mostrar un estado inicial o de esqueleto
            }
        }
    }
}

@Composable
fun ProfileHeader(
    profile: ProfileModel,
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profile.pathUrl,
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))
        ProfileTabBar(selectedTab, onTabSelected)
    }
}

@Composable
fun ProfileTabBar(selectedTab: ProfileTab, onTabSelected: (ProfileTab) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ProfileTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Button(
                onClick = { onTabSelected(tab) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(tab.title)
            }
        }
    }
}

@Composable
fun InfoContent(profile: ProfileModel) {
    // Función para calcular la edad
    fun calculateAge(birthdate: String): Int? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(birthdate, formatter)
            Period.between(birthDate, LocalDate.now()).years
        } catch (e: DateTimeParseException) {
            null
        }
    }

    val age = calculateAge(profile.birthdate)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Nombre: ${profile.name}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Email: ${profile.email}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        age?.let {
            Text("Edad: $it años", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }

        // CORREGIDO: Espacio mayor y estilo visual para la descripción
        Spacer(modifier = Modifier.height(24.dp))

        if (profile.summary.isNotBlank()) {
            Text(
                text = profile.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Text(
                text = "No hay descripción disponible.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Color desvanecido
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { /* Navegar a edición */ }) {
            Text("Editar Datos")
        }
    }
}

@Composable
fun PostsContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No hay posts para mostrar.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent() {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        DatePicker(state = datePickerState, modifier = Modifier.fillMaxWidth())
    }
}