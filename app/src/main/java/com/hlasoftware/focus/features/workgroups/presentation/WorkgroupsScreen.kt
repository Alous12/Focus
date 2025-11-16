package com.hlasoftware.focus.features.workgroups.presentation

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hlasoftware.focus.R
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
    onAddWorkgroup: () -> Unit,
    viewModel: WorkgroupsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId, showCreateWorkgroupSheet) {
        if (!showCreateWorkgroupSheet) {
            viewModel.loadWorkgroups(userId)
        }
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
                containerColor = colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.workgroups_add_button),
                    tint = colorScheme.onPrimary
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
                                WorkgroupCard(workgroup = workgroup)
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
                            color = colorScheme.error
                        )
                    }
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
                onWorkgroupCreated = {
                    viewModel.loadWorkgroups(userId)
                },
                onError = {
                    message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                },
                onClose = {
                    createWorkgroupViewModel.resetState()
                    onDismissCreateWorkgroupSheet()
                }
            )
        }
    }
}

@Composable
fun CreateWorkgroupContent(
    userId: String,
    viewModel: CreateWorkgroupViewModel,
    onWorkgroupCreated: () -> Unit,
    onError: (String) -> Unit,
    onClose: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is CreateWorkgroupUiState.Success -> {
                onWorkgroupCreated()
                onClose() // Close sheet on success
            }
            is CreateWorkgroupUiState.Error -> {
                onError(state.message)
                viewModel.resetState() // Reset after showing error
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
                onClick = { viewModel.createWorkgroup(name, description, imageUri, userId) },
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

        Box(modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }) {
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
            onValueChange = { name = it },
            label = { Text(stringResource(id = R.string.create_workgroup_name_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(id = R.string.create_workgroup_description_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /* TODO: Handle add members */ }) {
            Text(stringResource(id = R.string.create_workgroup_add_members_button))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.create_workgroup_add_members_later_prompt),
            style = typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun WorkgroupCard(workgroup: Workgroup) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
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
            Column {
                Text(
                    text = workgroup.name,
                    style = typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.workgroup_card_admin_label, workgroup.adminName),
                    style = typography.bodySmall
                )
            }
        }
    }
}
