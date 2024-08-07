package it.unibo.noteforall.ui.screen.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.createNotificationList
import it.unibo.noteforall.ui.composables.SingleNotification
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.Notification

@Composable
fun NotificationScreen(navController: NavHostController) {
    var isLaunched by remember { mutableStateOf(false) }
    val notificationList = remember { mutableStateListOf<Notification>() }

    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            createNotificationList(CurrentUserSingleton.currentUser!!.id, notificationList)
            isLaunched = true
        }
    }
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 5.dp, start = 10.dp, end = 10.dp)
    ) {
        items(notificationList) { notification ->
            SingleNotification(notification, navController)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}