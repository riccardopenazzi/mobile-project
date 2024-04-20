package it.unibo.noteforall.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class BottomNavigationItem(
    val title: String,
    val icon: ImageVector
)

val items = listOf(
    BottomNavigationItem(
        title = "Home",
        icon = Icons.Outlined.Home
    ),
    BottomNavigationItem(
        title = "Chat",
        icon = Icons.Outlined.Email
    ),
    BottomNavigationItem(
        title = "Settings",
        icon = Icons.Outlined.Settings
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBottomBar() {
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = false,
                onClick = {},
                label = {
                    Text(item.title)
                },
                alwaysShowLabel = true,
                icon = { BadgedBox(badge = null) {
                    item.icon
                } }
            ) }
    }
}

/*@Composable
fun bottomBar() {
    val bottomState = remember {
        mutableStateOf("Home")
    }

    Box {
        BottomNavigation(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
            backgroundColor = Color(0xFFFEDBD0),
            contentColor = Color(0xFF442c2E)
        ) {
            BottomNavigationItem(
                selected = bottomState.equals("Home"),
                onClick = { bottomState.equals("Home") },
                label = { Text(text = "Home") },
                icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) }
            )

            BottomNavigationItem(
                selected = bottomState.equals("Account"),
                onClick = { bottomState.equals("Account") },
                label = { Text(text = "Account") },
                icon = {
                    Icon(imageVector = Icons.Default.AccountCircle,
                        contentDescription = null)
                }
            )
            BottomNavigationItem(
                selected = bottomState.equals("Search"),
                onClick = { bottomState.equals("Search") },
                label = { Text(text = "Search") },
                icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) }
            )
            BottomNavigationItem(
                selected = bottomState.equals("Setting"),
                onClick = { bottomState.equals("Setting") },
                label = { Text(text = "Setting") },
                icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = null) }
            )
        }
    }
}*/
