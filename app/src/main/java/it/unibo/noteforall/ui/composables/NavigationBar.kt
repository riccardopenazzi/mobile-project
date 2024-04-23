package it.unibo.noteforall.ui.composables

import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import it.unibo.noteforall.utils.BottomNavigationItem
import it.unibo.noteforall.utils.navigation.NoteForAllRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBar(
    navController: NavHostController,
    items: List<BottomNavigationItem>,
    onItemSelected: (Int) -> Unit,
    selectedItemIndex: Int
) {
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    onItemSelected(index)
                    when (item.title) {
                        NoteForAllRoute.Home.title -> navController.navigate(NoteForAllRoute.Home.route)
                        NoteForAllRoute.Saved.title -> navController.navigate(NoteForAllRoute.Saved.route)
                        NoteForAllRoute.Search.title -> navController.navigate(NoteForAllRoute.Search.route)
                        NoteForAllRoute.Profile.title -> navController.navigate(NoteForAllRoute.Profile.route)
                    }
                },
                label = {
                    Text(item.title)
                },
                icon = {
                    BadgedBox(badge = {}) {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}
