package com.hlasoftware.focus.features.add_task.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.add_task.domain.model.Task
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class TaskCreationType {
    FOR_ME,
    FOR_ALL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    workgroupId: String,
    userId: String,
    memberIds: List<String>,
    onDismiss: () -> Unit,
    viewModel: AddTaskViewModel = koinViewModel(),
) {
    val sheetState = rememberModalBottomSheetState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var taskCreationType by remember { mutableStateOf(TaskCreationType.FOR_ME) }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddTaskUiState.Success -> {
                onDismiss()
            }
            is AddTaskUiState.Error -> {
                scope.launch { snackbarHostState.showSnackbar(state.message) }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { taskCreationType = TaskCreationType.FOR_ME }) {
                    Text(text = stringResource(id = R.string.add_task_create_for_me))
                }
                TextButton(onClick = { taskCreationType = TaskCreationType.FOR_ALL }) {
                    Text(text = stringResource(id = R.string.add_task_create_for_all))
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.add_activity_close))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text(stringResource(id = R.string.add_task_name_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text(stringResource(id = R.string.add_task_description_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(id = R.string.add_task_select_date_time))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Text(text = selectedDate ?: "dd/mm/aa", modifier = Modifier.clickable { showDatePicker = true })
                Text(text = selectedTime ?: "--:--", modifier = Modifier.clickable { showTimePicker = true })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.add_task_explanation_text),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val assignedIds = if (taskCreationType == TaskCreationType.FOR_ME) listOf(userId) else memberIds
                    val task = Task(
                        workgroupId = workgroupId,
                        name = taskName,
                        description = taskDescription,
                        creatorId = userId,
                        assignedMemberIds = assignedIds,
                        dueDate = selectedDate,
                        dueTime = selectedTime
                    )
                    viewModel.addTask(task)
                },
                enabled = taskName.isNotBlank() && uiState !is AddTaskUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.add_activity_create))
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { 
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = millis
                        selectedDate = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(calendar.time)
                    }
                    showDatePicker = false 
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        DatePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = { 
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    cal.set(Calendar.MINUTE, timePickerState.minute)
                    selectedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
                    showTimePicker = false 
                }) {
                    Text("OK")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}
