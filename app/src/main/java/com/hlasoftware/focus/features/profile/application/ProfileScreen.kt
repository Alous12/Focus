package com.hlasoftware.focus.features.profile.application

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import com.hlasoftware.focus.features.posts.domain.model.PostModel
import com.hlasoftware.focus.features.posts.presentation.CreatePostScreen
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class ProfileTab(val title: Int, val icon: ImageVector) {
    INFO(R.string.profile_tab_info, Icons.Default.Info),
    POSTS(R.string.profile_tab_posts, Icons.Default.Article),
    CALENDAR(R.string.profile_tab_calendar, Icons.Default.CalendarToday)
}

@Composable
fun ProfileScreen(
    userId: String,
    profileViewModel: ProfileViewModel = koinViewModel(),
    onLogout: () -> Unit,
) {
    val state by profileViewModel.state.collectAsState()
    val activities by profileViewModel.activities.collectAsState()
    val posts by profileViewModel.posts.collectAsState()
    var selectedTab by remember { mutableStateOf(ProfileTab.INFO) }
    var editingPost by remember { mutableStateOf<PostModel?>(null) }
    var showCreatePostScreen by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            profileViewModel.showProfile(userId)
        }
    }

    val onSave: (String, Uri?) -> Unit = { text, imageUri ->
        editingPost?.let {
            profileViewModel.updatePost(it.id, text, imageUri)
        } ?: run {
            profileViewModel.createPost(userId, text, imageUri)
        }
        showCreatePostScreen = false
        editingPost = null
    }

    val onCancel: () -> Unit = {
        showCreatePostScreen = false
        editingPost = null
    }

    if (showCreatePostScreen || editingPost != null) {
        CreatePostScreen(
            existingPost = editingPost,
            onSave = onSave,
            onCancel = onCancel
        )
    } else {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        DrawerContent(
                            onCloseDrawer = { scope.launch { drawerState.close() } },
                            onLogout = onLogout,
                            onDeleteAccount = { showDeleteAccountDialog = true }
                        )
                    }
                }
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.profile_title),
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                color = colorResource(id = R.color.title_color),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = stringResource(id = R.string.profile_menu))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        when (val currentState = state) {
                            is ProfileViewModel.ProfileUiState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            is ProfileViewModel.ProfileUiState.Success -> {
                                Column(Modifier.fillMaxSize()) {
                                    ProfileHeader(
                                        profile = currentState.profile,
                                        selectedTab = selectedTab,
                                        onTabSelected = { selectedTab = it }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Box(modifier = Modifier.weight(1f)) {
                                        when (selectedTab) {
                                            ProfileTab.INFO -> InfoContent(
                                                profile = currentState.profile,
                                                userId = userId,
                                                profileViewModel = profileViewModel
                                            )
                                            ProfileTab.POSTS -> PostsContent(
                                                posts = posts,
                                                onCreatePost = { showCreatePostScreen = true },
                                                onEditPost = { editingPost = it },
                                                onDeletePost = { profileViewModel.deletePost(it) }
                                            )
                                            ProfileTab.CALENDAR -> CalendarContent(
                                                userId,
                                                profileViewModel,
                                                activities
                                            )
                                        }
                                    }
                                }
                            }
                            is ProfileViewModel.ProfileUiState.Error -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        currentState.message,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                            ProfileViewModel.ProfileUiState.Init -> {}
                        }
                    }
                }
            }
        }
    }

    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            onConfirm = {
                profileViewModel.deleteAccount(userId) {
                    showDeleteAccountDialog = false
                    onLogout()
                }
            },
            onDismiss = { showDeleteAccountDialog = false }
        )
    }
}

@Composable
fun DrawerContent(onCloseDrawer: () -> Unit, onLogout: () -> Unit, onDeleteAccount: () -> Unit) {
    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(
                stringResource(id = R.string.profile_drawer_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.title_color)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(id = R.string.profile_drawer_delete_account),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onDeleteAccount)
            )
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onLogout) {
                Text(
                    stringResource(id = R.string.profile_drawer_logout),
                    color = Color.White,
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun DeleteAccountDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.delete_account_dialog_title),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 1.em,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            )
        },
        text = {
                 Row(
                     modifier = Modifier.fillMaxWidth(),
                     horizontalArrangement = Arrangement.SpaceEvenly
                 ) {
                     Button(
                         onClick = onDismiss,
                         colors = ButtonDefaults.buttonColors(
                             containerColor = colorResource(id = R.color.delete_account_no_button)
                         )
                     ) {
                         Text(
                             stringResource(id = R.string.delete_account_dialog_no),
                             color = Color.White
                         )
                     }
                     Button(
                         onClick = onConfirm,
                         colors = ButtonDefaults.buttonColors(
                             containerColor = colorResource(id = R.color.delete_account_yes_button)
                         )
                     ) {
                         Text(
                             stringResource(id = R.string.delete_account_dialog_yes),
                             color = Color.White
                         )
                     }
                 }
        },
        confirmButton = {},
        dismissButton = {}
    )
}


@Composable
fun ProfileHeader(profile: ProfileModel, selectedTab: ProfileTab, onTabSelected: (ProfileTab) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profile.pathUrl,
            contentDescription = stringResource(id = R.string.profile_header_photo_description),
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Card(
                modifier = Modifier.weight(1f).clickable { onTabSelected(tab) },
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.profile_tab_background)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(id = tab.title),
                        tint = if (isSelected) colorResource(id = R.color.title_color) else Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = tab.title),
                        color = if (isSelected) colorResource(id = R.color.title_color) else Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoContent(profile: ProfileModel, userId: String, profileViewModel: ProfileViewModel) {
    var showSummaryDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                profileViewModel.updateProfilePicture(userId, it)
            }
        }
    )

    if (showSummaryDialog) {
        EditDialog(
            title = stringResource(id = R.string.profile_edit_dialog_title),
            initialValue = profile.summary,
            onConfirm = { newSummary ->
                profileViewModel.updateSummary(userId, newSummary)
                showSummaryDialog = false
            },
            onDismiss = { showSummaryDialog = false }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(stringResource(id = R.string.profile_info_name, profile.name), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(id = R.string.profile_info_email, profile.email), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(24.dp))

        if (profile.summary.isNotBlank()) {
            Text(text = profile.summary, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        } else {
            Text(text = stringResource(id = R.string.profile_info_no_description), style = MaterialTheme.typography.bodyLarge, color = colorResource(id = R.color.profile_no_description_gray))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = { showSummaryDialog = true }) {
                Text(if (profile.summary.isNotBlank()) stringResource(id = R.string.profile_info_edit_description) else stringResource(id = R.string.profile_info_add_description))
            }
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text(stringResource(id = R.string.profile_info_change_photo))
            }
        }
    }
}

@Composable
fun EditDialog(title: String, initialValue: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.profile_edit_dialog_new_value)) }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }) {
                Text(stringResource(id = R.string.profile_edit_dialog_save))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.profile_edit_dialog_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsContent(
    posts: List<PostModel>,
    onCreatePost: () -> Unit,
    onEditPost: (PostModel) -> Unit,
    onDeletePost: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreatePost) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.profile_posts_create_post))
            }
        }
    ) { padding ->
        if (posts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.profile_posts_no_posts),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorResource(id = R.color.profile_no_description_gray)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(posts) { post ->
                    PostItem(
                        post = post,
                        onEdit = { onEditPost(post) },
                        onDelete = { onDeletePost(post.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PostItem(post: PostModel, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            post.imageUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = stringResource(id = R.string.profile_post_item_image_description),
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = post.text,
                modifier = Modifier.padding(16.dp)
            )
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            post.createdAt?.let {
                Text(
                    text = stringResource(id = R.string.profile_post_item_created, dateFormat.format(it)),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            post.updatedAt?.let {
                if (it != post.createdAt) {
                    Text(
                        text = stringResource(id = R.string.profile_post_item_edited, dateFormat.format(it)),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.profile_post_item_edit))
                }
                Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text(stringResource(id = R.string.profile_post_item_delete))
                }
            }
        }
    }
}


@Composable
fun CalendarContent(userId: String, viewModel: ProfileViewModel, activities: List<ActivityModel>) {
    val currentMonth = YearMonth.now()
    val startMonth = currentMonth.minusMonths(100)
    val endMonth = currentMonth.plusMonths(100)
    val firstDayOfWeek = firstDayOfWeekFromLocale()
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )
    val activitiesByDate = remember(activities) { activities.groupBy { LocalDate.parse(it.date) } }
    var showActivityDialog by remember { mutableStateOf<List<ActivityModel>>(emptyList()) }

    LaunchedEffect(state.firstVisibleMonth) {
        val visibleMonth = state.firstVisibleMonth.yearMonth
        viewModel.loadActivitiesForMonth(userId, visibleMonth.year, visibleMonth.monthValue)
    }

    if (showActivityDialog.isNotEmpty()) {
        ActivityDetailsDialog(activities = showActivityDialog) {
            showActivityDialog = emptyList()
        }
    }

    Column {
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                val dayActivities = activitiesByDate[day.date].orEmpty()
                Day(day, activities = dayActivities) {
                    if (dayActivities.isNotEmpty()) {
                        showActivityDialog = dayActivities
                    }
                }
            },
            monthHeader = {
                MonthHeader(it.yearMonth)
            }
        )
    }
}


@Composable
fun Day(day: CalendarDay, activities: List<ActivityModel>, onClick: (LocalDate) -> Unit) {
    val hasActivity = activities.isNotEmpty()
    val isToday = day.date == LocalDate.now() && day.position == DayPosition.MonthDate

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .border(
                width = if (isToday) 1.5.dp else 0.dp,
                color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .background(
                color = if (hasActivity) colorResource(id = R.color.calendar_day_with_activity) else Color.Transparent
            )
            .clickable(enabled = day.position == DayPosition.MonthDate) {
                onClick(day.date)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (hasActivity) colorResource(id = R.color.calendar_day_text_with_activity) else if (day.position == DayPosition.MonthDate) MaterialTheme.colorScheme.onBackground else colorResource(id = R.color.calendar_day_text_not_in_month),
            fontSize = 12.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
        if (hasActivity) {
            Text(
                text = activities.first().title,
                color = colorResource(id = R.color.calendar_day_text_with_activity),
                fontSize = 8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MonthHeader(yearMonth: YearMonth) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatter.format(yearMonth),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
    Row(Modifier.fillMaxWidth()) {
        for (dayOfWeek in DayOfWeek.values()) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}

@Composable
fun ActivityDetailsDialog(activities: List<ActivityModel>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.profile_calendar_activity_details_title)) },
        text = {
            LazyColumn {
                items(activities) { activity ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = activity.title, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = activity.description)
                        activity.startTime?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Hora: $it", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.profile_calendar_activity_details_close))
            }
        }
    )
}
