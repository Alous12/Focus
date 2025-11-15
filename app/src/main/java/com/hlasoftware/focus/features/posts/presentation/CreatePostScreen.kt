package com.hlasoftware.focus.features.posts.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.posts.domain.model.PostModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    existingPost: PostModel? = null,
    onSave: (text: String, imageUri: Uri?) -> Unit,
    onCancel: () -> Unit,
) {
    var text by remember { mutableStateOf(existingPost?.text ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val existingImageUrl = existingPost?.imageUrl

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = if (existingPost == null) R.string.create_post_title else R.string.edit_post_title)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.create_post_back_button))
                    }
                },
                actions = {
                    Button(onClick = { onSave(text, imageUri) }) {
                        Text(stringResource(id = R.string.create_post_save_button))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                placeholder = { Text(stringResource(id = R.string.create_post_placeholder)) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            val imageUrl = imageUri?.toString() ?: existingImageUrl
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = stringResource(id = R.string.create_post_selected_image),
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text(stringResource(id = R.string.create_post_select_image_button))
            }
        }
    }
}
