package com.hlasoftware.focus.features.add_member.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.add_member.domain.model.User
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    onBack: () -> Unit,
    onAddMembers: (List<User>) -> Unit,
    viewModel: AddMemberViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.addMemberStatus) {
        if (uiState.addMemberStatus == AddMemberStatus.Success) {
            onAddMembers(uiState.selectedUsers.values.toList())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.add_member_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.add_member_back_button)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(id = R.string.add_member_search_placeholder)) }
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.searchResults) { user ->
                        UserItem(user = user, isSelected = uiState.selectedUsers.containsKey(user.id)) {
                            viewModel.onUserSelected(user)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { onBack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.add_member_cancel_button))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { viewModel.onConfirmSelection() },
                    modifier = Modifier.weight(1f),
                    enabled = uiState.selectedUsers.isNotEmpty() && uiState.addMemberStatus != AddMemberStatus.Loading
                ) {
                    if (uiState.addMemberStatus == AddMemberStatus.Loading) {
                        CircularProgressIndicator()
                    } else {
                        Text(text = stringResource(id = R.string.add_member_add_button))
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User, isSelected: Boolean, onUserSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onUserSelected() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: Replace with user avatar
        Text(
            text = user.name,
            modifier = Modifier.weight(1f)
        )
        Checkbox(checked = isSelected, onCheckedChange = { onUserSelected() })
    }
}
