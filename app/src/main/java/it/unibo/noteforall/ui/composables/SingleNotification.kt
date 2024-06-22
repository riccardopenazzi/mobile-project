package it.unibo.noteforall.ui.composables

import android.text.format.DateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import it.unibo.noteforall.utils.Notification
import it.unibo.noteforall.utils.navigation.NoteForAllRoute

@Composable
fun SingleNotification(notification: Notification, navController: NavHostController) {

    Column(
        modifier = Modifier
            .border(
                BorderStroke(2.dp, if (notification.isRead) Color.Black else Color.Red),
                RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
            .fillMaxSize()
            .clickable {
                if (notification.postTarget != "") {
                    navController.navigate(NoteForAllRoute.ViewNote.buildRoute(notification.postTarget))
                }
            }
    ) {
        Row (
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            val notificationDate = DateFormat.format("dd/MM/yyyy", notification.date).toString()
            Text(text = notificationDate, fontSize = 12.sp, fontStyle = FontStyle.Italic)
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(4.dp))
            AsyncImage(
                model = notification.sourcePicRef,
                contentDescription = "user source image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = notification.content, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}