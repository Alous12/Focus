package com.hlasoftware.focus.features.workgroups.presentation

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.add_member.domain.model.User
import com.hlasoftware.focus.features.create_workgroup.presentation.CreateWorkgroupUiState
import com.hlasoftware.focus.features.create_workgroup.presentation.CreateWorkgroupViewModel
import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkgroupsScreen(
    userId: String,
    showCreateWorkgroupSheet: Boolean,
    onDismissCreateWorkgroupSheet: () -> Unit,
    showWorkgroupOptions: Boolean,
    onDismissWorkgroupOptions: () -> Unit,
    onAddWorkgroup: () -> Unit,
    onCreateWorkgroup: () -> Unit,
    onJoinWorkgroup: () -> Unit,
    onWorkgroupClick: (String) -> Unit,
    onNavigateToAddMember: () -> Unit,
    navBackStackEntry: NavBackStackEntry,
    viewModel: WorkgroupsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val leaveState by viewModel.leaveState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var workgroupToDelete by remember { mutableStateOf<Workgroup?>(null) }
    var workgroupToLeave by remember { mutableStateOf<Workgroup?>(null) }

    var newGroupName by rememberSaveable { mutableStateOf("") }
    var newGroupDescription by rememberSaveable { mutableStateOf("") }
    var newGroupImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var selectedMembersForNewGroup by rememberSaveable { mutableStateOf<List<User>>(emptyList()) }

    val newMembersResult = navBackStackEntry.savedStateHandle.get<List<User>>("selectedMembers")

    LaunchedEffect(newMembersResult) {
        if (newMembersResult != null) {
            selectedMembersForNewGroup = newMembersResult
            navBackStackEntry.savedStateHandle.remove<List<User>>("selectedMembers")
        }
    }


    LaunchedEffect(userId) {
        viewModel.listenToWorkgroups(userId)
    }

    LaunchedEffect(deleteState) {
        when (val state = deleteState) {
            is DeleteWorkgroupUiState.Success -> {
                scope.launch { snackbarHostState.showSnackbar("Grupo eliminado con éxito") }
                viewModel.resetDeleteState()
            }
            is DeleteWorkgroupUiState.Error -> {
                scope.launch { snackbarHostState.showSnackbar(state.message) }
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    LaunchedEffect(leaveState) {
        when (val state = leaveState) {
            is LeaveWorkgroupUiState.Success -> {
                scope.launch { snackbarHostState.showSnackbar("Has abandonado el grupo") }
                viewModel.resetLeaveState()
            }
            is LeaveWorkgroupUiState.Error -> {
                scope.launch { snackbarHostState.showSnackbar(state.message) }
                viewModel.resetLeaveState()
            }
            else -> {}
        }
    }

    if (workgroupToDelete != null) {
        AlertDialog(
            onDismissRequest = { workgroupToDelete = null },
            title = { Text(stringResource(id = R.string.workgroup_delete_dialog_title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        workgroupToDelete?.let { viewModel.deleteWorkgroup(it.id) }
                        workgroupToDelete = null
                    }
                ) {
                    Text(stringResource(id = R.string.workgroup_delete_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { workgroupToDelete = null }) {
                    Text(stringResource(id = R.string.workgroup_delete_dialog_cancel))
                }
            }
        )
    }

    if (workgroupToLeave != null) {
        AlertDialog(
            onDismissRequest = { workgroupToLeave = null },
            title = { Text(stringResource(id = R.string.workgroup_details_leave_dialog_title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        workgroupToLeave?.let { viewModel.leaveWorkgroup(it.id, userId) }
                        workgroupToLeave = null
                    }
                ) {
                    Text(stringResource(id = R.string.workgroup_details_leave_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { workgroupToLeave = null }) {
                    Text(stringResource(id = R.string.workgroup_details_delete_dialog_cancel))
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.workgroups_title)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddWorkgroup,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.workgroups_add_button),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is WorkgroupsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WorkgroupsUiState.Success -> {
                    if (state.workgroups.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(id = R.string.workgroups_no_groups))
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.workgroups) { workgroup ->
                                WorkgroupCard(
                                    workgroup = workgroup,
                                    userId = userId,
                                    onClick = { onWorkgroupClick(workgroup.id) },
                                    onDeleteClick = { workgroupToDelete = workgroup },
                                    onLeaveClick = { workgroupToLeave = workgroup }
                                )
                            }
                        }
                    }
                }
                is WorkgroupsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    if (showWorkgroupOptions) {
        ModalBottomSheet(
            onDismissRequest = onDismissWorkgroupOptions,
            sheetState = rememberModalBottomSheetState(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.workgroup_options_menu_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = onCreateWorkgroup,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.workgroup_options_create))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onJoinWorkgroup,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.workgroup_options_join))
                }
            }
        }
    }

    if (showCreateWorkgroupSheet) {
        val createWorkgroupViewModel: CreateWorkgroupViewModel = koinViewModel()

        ModalBottomSheet(
            onDismissRequest = {
                createWorkgroupViewModel.resetState()
                onDismissCreateWorkgroupSheet()
            },
            sheetState = rememberModalBottomSheetState(),
        ) {
            CreateWorkgroupContent(
                userId = userId,
                viewModel = createWorkgroupViewModel,
                name = newGroupName, onNameChange = { newGroupName = it },
                description = newGroupDescription, onDescriptionChange = { newGroupDescription = it },
                imageUri = newGroupImageUri, onImageUriChange = { newGroupImageUri = it },
                selectedMembers = selectedMembersForNewGroup,
                onError = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                },
                onClose = {
                    createWorkgroupViewModel.resetState()
                    onDismissCreateWorkgroupSheet()
                    // Clear state on close
                    newGroupName = ""
                    newGroupDescription = ""
                    newGroupImageUri = null
                    selectedMembersForNewGroup = emptyList()
                },
                onNavigateToAddMember = onNavigateToAddMember
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreateWorkgroupContent(
    userId: String,
    viewModel: CreateWorkgroupViewModel,
    name: String, onNameChange: (String) -> Unit,
    description: String, onDescriptionChange: (String) -> Unit,
    imageUri: Uri?, onImageUriChange: (Uri?) -> Unit,
    selectedMembers: List<User>,
    onError: (String) -> Unit,
    onClose: () -> Unit,
    onNavigateToAddMember: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val permissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> onImageUriChange(uri) }
    )

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is CreateWorkgroupUiState.Success -> {
                onClose()
            }
            is CreateWorkgroupUiState.Error -> {
                onError(state.message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.create_workgroup_close))
            }
            TextButton(
                onClick = { 
                    val memberIds = selectedMembers.map { it.id }
                    viewModel.createWorkgroup(name, description, imageUri, userId, memberIds) 
                },
                enabled = name.isNotBlank() && description.isNotBlank() && uiState !is CreateWorkgroupUiState.Loading
            ) {
                if (uiState is CreateWorkgroupUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(id = R.string.create_workgroup_title))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(id = R.string.create_workgroup_image_prompt))
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.clickable { 
            if (permissionState.status.isGranted) {
                imagePickerLauncher.launch("image/*")
            } else {
                permissionState.launchPermissionRequest()
            }
        }) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your placeholder
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(id = R.string.create_workgroup_name_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(id = R.string.create_workgroup_description_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (selectedMembers.isNotEmpty()) {
            Text("Miembros seleccionados:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(selectedMembers) { member ->
                    Text(member.name)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = onNavigateToAddMember) {
            Text(stringResource(id = R.string.create_workgroup_add_members_button))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.create_workgroup_add_members_later_prompt),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun WorkgroupCard(
    workgroup: Workgroup,
    userId: String,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLeaveClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = workgroup.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground) // Replace with your placeholder
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workgroup.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.workgroup_card_admin_label, workgroup.adminName),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(id = R.string.workgroup_details_options_button)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (workgroup.admin == userId) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.workgroup_delete_option)) },
                            onClick = {
                                onDeleteClick()
                                showMenu = false
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.workgroup_details_leave_option)) },
                            onClick = {
                                onLeaveClick()
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}
