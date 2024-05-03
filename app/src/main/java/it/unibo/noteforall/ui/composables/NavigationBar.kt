package it.unibo.noteforall.ui.composables

import android.util.Log
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.navigation.NavHostController
import it.unibo.noteforall.utils.navigation.BottomNavigationItem
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBar(
    navController: NavHostController,
    items: List<BottomNavigationItem>,
    onItemSelected: (Int) -> Unit,
    selectedItemIndex: AtomicInteger,
    currentRoute: NoteForAllRoute
) {
    NavigationBar {
        Log.d("test", selectedItemIndex.toString())
        selectedItemIndex.set(NoteForAllRoute.routes.indexOf(currentRoute))
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex.get() == index,
                onClick = {
                    onItemSelected(index)
                    when (item.title) {
                        NoteForAllRoute.Home.title -> navController.navigate(NoteForAllRoute.Home.route)
                        NoteForAllRoute.Saved.title -> navController.navigate(NoteForAllRoute.Saved.route)
                        NoteForAllRoute.Search.title -> navController.navigate(NoteForAllRoute.Search.route)
                        NoteForAllRoute.MyProfile.title -> navController.navigate(NoteForAllRoute.MyProfile.route)
                    }
                },
                label = {
                    Text(item.title)
                },
                icon = {
                    BadgedBox(badge = {}) {
                        Icon(
                            imageVector = if (index == selectedItemIndex.get()) {
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
