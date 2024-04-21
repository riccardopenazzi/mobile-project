package it.unibo.noteforall.ui.screen.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.TitleCard

@Composable
fun LoginScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight()
            ) {
                Text(
                    text = "Note For All",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(vertical = 50.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                listOf(
                    "Email or username",
                    "Password"
                ).forEach { label ->
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = {
                        Text(text = label)
                    })
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Login")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { /*TODO*/ }, shape = RoundedCornerShape(50), border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(text = "Don't have an account?")
                }
            }
        }
    }
}