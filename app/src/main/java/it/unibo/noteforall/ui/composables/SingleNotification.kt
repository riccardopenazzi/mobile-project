package it.unibo.noteforall.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.getUserPicFromId
import it.unibo.noteforall.utils.Notification

@Composable
fun SingleNotification(notification: Notification) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.border(BorderStroke(2.dp, if (notification.isRead) Color.Black else Color.Red), RoundedCornerShape(8.dp)).padding(6.dp).fillMaxSize()
    ) {
        AsyncImage(
            model = notification.sourcePicRef,
            contentDescription = "user source image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = notification.content, fontSize = 18.sp)
    }
}