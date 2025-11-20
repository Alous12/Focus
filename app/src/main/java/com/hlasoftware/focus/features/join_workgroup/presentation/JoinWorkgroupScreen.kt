package com.hlasoftware.focus.features.join_workgroup.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hlasoftware.focus.R
import org.koin.androidx.compose.koinViewModel
import com.hlasoftware.focus.features.join_workgroup.presentation.JoinWorkgroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinWorkgroupScreen(
    userId: String,
    onJoinSuccess: () -> Unit,
    onCancel: () -> Unit,
    viewModel: JoinWorkgroupViewModel = koinViewModel(),
) {
    var workgroupCode by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is JoinWorkgroupUiState.Success) {
            onJoinSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.join_workgroup_title)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.activity_details_back_button)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    value = workgroupCode,
                    onValueChange = { workgroupCode = it },
                    label = { Text(stringResource(id = R.string.join_workgroup_code_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState is JoinWorkgroupUiState.Error
                )

                if (uiState is JoinWorkgroupUiState.Error) {
                    Text(
                        text = (uiState as JoinWorkgroupUiState.Error).message,
                        color = colorScheme.error,
                        style = typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (uiState is JoinWorkgroupUiState.Loading) {
                        CircularProgressIndicator()
                    } else {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.join_workgroup_cancel_button))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { viewModel.joinWorkgroup(userId, workgroupCode) },
                            enabled = workgroupCode.isNotBlank(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.join_workgroup_join_button))
                        }
                    }
                }
            }
        }
    }
}
