package it.unibo.noteforall.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import it.unibo.noteforall.ui.composables.AppBar

@Composable
fun EditProfileScreen() {
    Scaffold(
        topBar = { AppBar(title = "Edit Profile") }
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
                    listOf(
                        "Name",
                        "Surname",
                        "Username",
                        "Password",
                        "Repeat password"
                    ).forEach { label ->
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = "", onValueChange = {}, label = {
                            Text(text = label)
                        }, modifier = Modifier.fillMaxWidth())
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 60.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Cancel", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Green
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Save", color = Color.White)
                        }
                    }

                }
            }
        }
    }
}