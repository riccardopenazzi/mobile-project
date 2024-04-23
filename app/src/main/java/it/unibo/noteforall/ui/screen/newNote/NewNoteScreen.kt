package it.unibo.noteforall.ui.screen.newNote

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.unibo.noteforall.ui.composables.AppBar
import it.unibo.noteforall.ui.theme.Teal800

@Composable
fun NewNoteScreen() {
    Scaffold(
        //topBar = { AppBar(title = "New Note") }
    ) {contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
        ) {//min padding 56
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = {
                        Text(text = "Title")
                    }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = {
                        Text(text = "Category")
                    }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = {
                        Text(text = "Description")
                    }, modifier = Modifier.fillMaxWidth(), minLines = 10)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row (
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Outlined.AttachFile, contentDescription = "Choose note")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Upload note",
                            modifier = Modifier
                                .border(1.dp, Teal800, RoundedCornerShape(30))
                                .padding(6.dp)
                                .width(180.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row (
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Outlined.Image, contentDescription = "Choose note preview")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Choose note preview",
                            modifier = Modifier
                                .border(1.dp, Teal800, RoundedCornerShape(30))
                                .padding(6.dp)
                                .width(180.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Green
                            )
                        ) {
                            Text(text = "Save", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}