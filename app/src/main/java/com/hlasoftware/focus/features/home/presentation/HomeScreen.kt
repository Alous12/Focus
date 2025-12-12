package com.hlasoftware.focus.features.home.presentation

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    userId: String,
    homeViewModel: IHomeViewModel = koinViewModel<HomeViewModel>(),
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    showAddActivitySheet: Boolean,
    onDismissAddActivitySheet: () -> Unit,
    onActivityClick: (String) -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val showDeleteDialog by homeViewModel.showDeleteConfirmationDialog.collectAsState()

    // Request notification permission on Android 13+
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        )
        LaunchedEffect(Unit) {
            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }

    LaunchedEffect(userId, selectedDate) {
        homeViewModel.loadHome(userId, selectedDate)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { homeViewModel.onDismissDeleteActivity() },
            title = { Text(stringResource(id = R.string.delete_activity_dialog_title)) },
            confirmButton = {
                TextButton(onClick = { homeViewModel.onConfirmDeleteActivity(userId, selectedDate) }) {
                    Text(stringResource(id = R.string.delete_activity_dialog_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { homeViewModel.onDismissDeleteActivity() }) {
                    Text(stringResource(id = R.string.delete_activity_dialog_no))
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopAppBar(
                title = { Text(stringResource(id = R.string.home_title), style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.title_color)) }
            )


            Spacer(modifier = Modifier.height(16.dp))

            DateSelector(
                currentDate = selectedDate,
                onPreviousDay = { onDateChange(selectedDate.minusDays(1)) },
                onNextDay = { onDateChange(selectedDate.plusDays(1)) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is HomeUiState.Success -> {
                    if (state.activities.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.home_no_activities),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    } else {
                        ActivitiesList(
                            activities = state.activities,
                            onDeleteClicked = { activityId ->
                                homeViewModel.onDeleteActivityClicked(activityId)
                            },
                            onActivityClicked = onActivityClick
                        )
                    }
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.home_error_message, state.message),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        if (showAddActivitySheet) {
            ModalBottomSheet(
                onDismissRequest = onDismissAddActivitySheet,
                sheetState = rememberModalBottomSheetState(),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                AddActivityContent(
                    userId = userId,
                    homeViewModel = homeViewModel,
                    onClose = onDismissAddActivitySheet,
                )
            }
        }
    }
}

@Composable
fun AddActivityContent(
    userId: String,
    homeViewModel: IHomeViewModel,
    onClose: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf<LocalTime?>(null) }

    val context = LocalContext.current

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year: Int, month: Int, dayOfMonth: Int ->
            date = LocalDate.of(year, month + 1, dayOfMonth)
        },
        date.year,
        date.monthValue - 1,
        date.dayOfMonth
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            time = LocalTime.of(hour, minute)
        },
        LocalTime.now().hour,
        LocalTime.now().minute,
        true // 24 hour format
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.add_activity_close))
            }
            TextButton(
                onClick = {
                    homeViewModel.createActivity(userId, title, description, date, time)
                    onClose()
                },
                enabled = title.isNotBlank()
            ) {
                Text(
                    text = stringResource(id = R.string.add_activity_create),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(id = R.string.add_activity_name_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(id = R.string.add_activity_description_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.add_activity_select_date_time),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { datePickerDialog.show() }
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = stringResource(id = R.string.add_activity_date_label))
                Spacer(modifier = Modifier.width(8.dp))
                Text(date.format(DateTimeFormatter.ofPattern("dd/MM/yy")))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { timePickerDialog.show() }
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = stringResource(id = R.string.add_activity_time_label))
                Spacer(modifier = Modifier.width(8.dp))
                Text(time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: stringResource(id = R.string.add_activity_time_placeholder))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.add_activity_optional_time),
            style = MaterialTheme.typography.bodySmall,
            color = colorResource(id = R.color.optional_text_gray)
        )
    }
}

@Composable
fun DateSelector(
    currentDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern(stringResource(id = R.string.date_format), Locale("es", "ES"))
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
            Icon(
                Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = stringResource(id = R.string.date_selector_previous_day),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = formattedDate,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextDay) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = stringResource(id = R.string.date_selector_next_day),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ActivitiesList(
    activities: List<ActivityModel>,
    onDeleteClicked: (String) -> Unit,
    onActivityClicked: (String) -> Unit,
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
                onDeleteClicked = { onDeleteClicked(activity.id) },
                onActivityClicked = { onActivityClicked(activity.id) }
            )
        }
    }
}

@Composable
fun ActivityCard(
    activity: ActivityModel,
    onDeleteClicked: () -> Unit,
    onActivityClicked: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val indicatorColor = when (activity.type) {
        "CLASS" -> colorResource(id = R.color.indicator_class)
        "TASK" -> colorResource(id = R.color.indicator_task)
        "MEETING" -> colorResource(id = R.color.indicator_meeting)
        else -> colorResource(id = R.color.indicator_default)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onActivityClicked() },
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
                activity.startTime?.let {
                    val endTimeText = activity.endTime?.let { e -> stringResource(id = R.string.activity_start_end_time, it, e) } ?: it
                    Text(
                        text = endTimeText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(id = R.string.activity_card_options),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.activity_card_delete), color = colorResource(id = R.color.delete_red)) },
                        onClick = {
                            onDeleteClicked()
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}
