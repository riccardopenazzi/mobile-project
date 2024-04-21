package it.unibo.noteforall.ui.screen.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn

@Composable
fun SignupScreen() {
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
                    modifier = Modifier.padding(vertical = 30.dp)
                )
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Select profile image",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { /* TODO */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
                listOf(
                    "Name",
                    "Surname",
                    "Email",
                    "Username",
                    "Password",
                    "Repeat password"
                ).forEach { label ->
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = {
                        Text(text = label)
                    })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Signup")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "Already have an account?")
                }
            }
        }
    }
}
