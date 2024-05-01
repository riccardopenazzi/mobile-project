package it.unibo.noteforall.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.unibo.noteforall.ui.theme.Teal800
import it.unibo.noteforall.utils.navigation.NoteForAllRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(isExtended: Boolean = false, navController: NavHostController) {
    Card (
        onClick = { if (!isExtended) navController.navigate(NoteForAllRoute.ViewNote.route) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        Column (
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = if (isExtended) Alignment.Start else Alignment.CenterHorizontally
        ) {
            /*Author*/
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Author", modifier = Modifier.weight(1f))
                val starIcon = remember { mutableStateOf(Icons.Outlined.StarBorder) }
                IconButton(onClick = {
                    when (starIcon.value) {
                        Icons.Outlined.StarBorder -> starIcon.value = Icons.Filled.Star
                        else -> starIcon.value = Icons.Outlined.StarBorder
                    }
                }) {
                    Icon(
                        imageVector = starIcon.value,
                        contentDescription = ""
                    )
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            Icon(imageVector = Icons.Rounded.Image, contentDescription = "Note preview", modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "Note title", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Category",
                modifier = Modifier
                    .border(1.dp, Teal800, RoundedCornerShape(30))
                    .padding(6.dp)
            )
            if (isExtended) {
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "NOTE DESCRIPTION", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.size(10.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Download, "Download icon")
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Download note",
                        modifier = Modifier
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(30))
                            .padding(6.dp)
                    )
                }
            }
        }
    }
}
