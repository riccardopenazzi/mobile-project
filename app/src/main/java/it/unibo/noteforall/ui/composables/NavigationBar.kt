package it.unibo.noteforall.ui.composables

import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import it.unibo.noteforall.utils.BottomNavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(
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