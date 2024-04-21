package it.unibo.noteforall.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard() {
    Card(
        onClick = { /*TODO()*/ },
        modifier = Modifier.padding(8.dp)
    ) {
        Column (
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*Author*/
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = "", modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Author", modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Outlined.StarBorder, contentDescription = "")
            }
            Spacer(modifier = Modifier.size(10.dp))
            Icon(imageVector = Icons.Outlined.Image, contentDescription = "Note preview", modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "Note title", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(10.dp))
            AssistChip(onClick = { }, label = { Text(text = "Category")})
        }
    }
}