package com.hlasoftware.focus.features.add_routines.presentation

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hlasoftware.focus.ui.theme.FocusTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoutineScreen(
    onClose: () -> Unit,
    viewModel: AddRoutineViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var routineName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val daysOrder = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val days = remember { mutableStateMapOf<String, Boolean>().apply { daysOrder.forEach { put(it, false) } } }

    var startTime by remember { mutableStateOf<String?>(null) }
    var endTime by remember { mutableStateOf<String?>(null) }

    val colorOptions = listOf(
        Color(0xFFE57373), Color(0xFF81C784), Color(0xFFFFD54F), Color(0xFF64B5F6), Color(0xFFBA68C8)
    )
    var selectedColor by remember { mutableStateOf(colorOptions[0]) }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddRoutineUiState.Success -> {
                Toast.makeText(context, "Rutina creada con éxito", Toast.LENGTH_SHORT).show()
                onClose()
            }
            is AddRoutineUiState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurface) } },
                actions = {
                    TextButton(onClick = {
                        val selectedDays = days.filter { it.value }.keys.toList()
                        viewModel.createRoutine(routineName, description, selectedDays, startTime, endTime, selectedColor.toArgb())
                    }) {
                        Text("Crear", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                TextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    placeholder = { Text("Nombre de la rutina", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary, unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary, focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Frecuencia", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Regularidad:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            daysOrder.forEach { day -> DayOfWeekSelector(day = day, isSelected = days[day] ?: false, onDaySelected = { days[it] = !(days[it] ?: false) }) }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Horario (opcional):", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            TimeSelector(label = "Inicio:", selectedTime = startTime, onTimeSelected = { startTime = it })
                            TimeSelector(label = "Fin:", selectedTime = endTime, onTimeSelected = { endTime = it })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Información de la rutina", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tarjeta:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(12.dp))
                        ColorSelector(options = colorOptions, selected = selectedColor, onColorSelected = { selectedColor = it })
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Escribir descripción", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary, focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Definir el horario es opcional, puedes no escoger hora de inicio y de fin.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
            }
            if (uiState is AddRoutineUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun DayOfWeekSelector(day: String, isSelected: Boolean, onDaySelected: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onDaySelected(day) }) {
        Text(text = day, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.size(24.dp).border(BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant), shape = RoundedCornerShape(4.dp)))
    }
}

@Composable
private fun TimeSelector(label: String, selectedTime: String?, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, min -> onTimeSelected("%02d:%02d".format(hour, min)) },
        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
    )

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { timePickerDialog.show() }) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(selectedTime ?: "--:--", color = if (selectedTime != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Outlined.Schedule, contentDescription = "Seleccionar hora", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun ColorSelector(options: List<Color>, selected: Color, onColorSelected: (Color) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        options.forEach { color ->
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color).clickable { onColorSelected(color) }) {
                if (color == selected) {
                    Box(modifier = Modifier.fillMaxSize().border(2.dp, MaterialTheme.colorScheme.background, CircleShape))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddRoutineScreenPreview() {
    FocusTheme(darkTheme = true) { AddRoutineScreen(onClose = {}) }
}
