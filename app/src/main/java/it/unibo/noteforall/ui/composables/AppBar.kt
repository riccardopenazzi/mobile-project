package it.unibo.noteforall.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.checkExistNewNotification
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.getCategoriesList
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.loadHomePosts
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.readAllNotifications
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.Note
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController? = null,
    currentRoute: NoteForAllRoute,
    posts: MutableList<Note>
) {
    var showFiltersDialog by remember { mutableStateOf(false) }
    val categories = remember { mutableStateListOf<String>() }
    var newNotification by remember { mutableStateOf(false) }

    getCategoriesList(categories)

    if (showFiltersDialog) {
        FiltersDialog(
            categories = categories,
            onDismiss = { showFiltersDialog = false },
            onConfirm = { showFiltersDialog = false },
            clearFilters = {
                when (currentRoute.title) {
                    NoteForAllRoute.Home.title ->
                        CoroutineScope(Dispatchers.Main).launch {
                            posts.clear()
                            loadHomePosts(posts)
                        }

                    NoteForAllRoute.Search.title ->
                        CoroutineScope(Dispatchers.Main).launch {
                            posts.clear()
                        }

                    else -> {}
                }
                showFiltersDialog = false
            },
            posts
        )
    }

    CenterAlignedTopAppBar(
        title = {
            Text(
                currentRoute.title,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            if (
                (currentRoute.title == NoteForAllRoute.ViewNote.title) ||
                (currentRoute.title == NoteForAllRoute.Profile.title) ||
                (currentRoute.title == NoteForAllRoute.EditProfile.title) ||
                (currentRoute.title == NoteForAllRoute.NewNote.title) ||
                (currentRoute.title == NoteForAllRoute.Settings.title) ||
                (currentRoute.title == NoteForAllRoute.Notifications.title)
            ) {
                IconButton(onClick = {
                    if (currentRoute.title == NoteForAllRoute.Notifications.title) {
                        CoroutineScope(Dispatchers.Main).launch {
                            readAllNotifications(CurrentUserSingleton.currentUser!!.id)
                        }
                    }
                    navController?.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Back button"
                    )
                }
            }
            if (currentRoute.title == NoteForAllRoute.MyProfile.title) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController?.navigate(NoteForAllRoute.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Settings"
                        )
                    }
                    LaunchedEffect(Unit) {
                        newNotification =
                            checkExistNewNotification(CurrentUserSingleton.currentUser!!.id)
                    }
                    IconButton(onClick = { navController?.navigate(NoteForAllRoute.Notifications.route) }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            tint = if (newNotification) Color.Red else MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Notifications"
                        )
                    }
                }
            }
        },
        actions = {
            if (
                (currentRoute.title == NoteForAllRoute.Home.title) ||
                (currentRoute.title == NoteForAllRoute.Saved.title) ||
                (currentRoute.title == NoteForAllRoute.Search.title)
            ) {
                IconButton(onClick = { showFiltersDialog = true }) {
                    Icon(
                        imageVector = Icons.Outlined.FilterAlt,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Filter button"
                    )
                }
            } else if (currentRoute.title == NoteForAllRoute.MyProfile.title) {
                IconButton(onClick = {
                    navController?.navigate(NoteForAllRoute.EditProfile.route)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Edit profile button"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        )
    )
}
