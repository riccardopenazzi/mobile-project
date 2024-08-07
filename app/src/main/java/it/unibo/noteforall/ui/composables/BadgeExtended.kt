package it.unibo.noteforall.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

@Composable
fun BadgeExtended(title: String, imageRef: String, instructions: String, isUnlocked: Boolean) {

    var isExtended by remember { mutableStateOf(false) }
    if (!isExtended) {
        IconButton(onClick = { isExtended = !isExtended }) {
            AsyncImage(
                model = imageRef,
                contentDescription = "badge image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .alpha(if (isUnlocked) 1f else 0.5f)
            )
            Spacer(Modifier.width(10.dp))
        }
    } else {
        Dialog(
            onDismissRequest = { isExtended = !isExtended },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                elevation = CardDefaults.cardElevation(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(3 / 5f),
                shape = RoundedCornerShape(5),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(start = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        AsyncImage(
                            model = imageRef,
                            contentDescription = "badge image",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .alpha(if (isUnlocked) 1f else 0.5f)
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(text = title, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp))
                        Spacer(Modifier.height(20.dp))
                        Text(text = instructions, style = TextStyle(fontStyle = FontStyle.Italic, fontSize = 18.sp))
                    }
                }
            }
        }
    }
}