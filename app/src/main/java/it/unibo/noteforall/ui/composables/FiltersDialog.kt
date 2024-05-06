package it.unibo.noteforall.ui.composables

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.Timestamp
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.applyFilters
import it.unibo.noteforall.utils.Note
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    clearFilters: () -> Unit,
    posts: MutableList<Note>
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var showAlertDialog by remember { mutableStateOf(false) }
    val dateSelected = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Input
    )

    if (showAlertDialog) {
        MyAlertDialog(
            onDismissRequest = { showAlertDialog = false },
            onConfirmation = {
                showAlertDialog = false
                clearFilters()
            },
            title = "Reset all filters?",
            text = "All filters selected or applied before will be removed and the research will be lost.",
            icon = null
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        val scrollState = rememberScrollState()
        Card (
            elevation = CardDefaults.cardElevation(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(4 / 5f)
                .verticalScroll(scrollState),
            shape = RoundedCornerShape(5),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = "Filters",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Posts from: ")
                    DatePicker(state = dateSelected)
                }
                Box {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            label = { Text("Choose the category: ") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor(),
                            colors = outlinedTextFieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(5))
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(text = category) },
                                    onClick = {
                                        expanded = false
                                        selectedCategory = category
                                    }
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    OutlinedButton(
                        onClick = {
                            showAlertDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.errorContainer,
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.errorContainer),
                    ) {
                        Text("Reset filters")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        modifier = Modifier.padding(8.dp),
                        onClick = onDismiss
                    ) {
                        Text(text = "Dismiss", color = MaterialTheme.colorScheme.secondary)
                    }
                    TextButton(
                        modifier = Modifier.padding(8.dp),
                        onClick = {
                            onConfirm()
                            val dateSelectedMillis = dateSelected.selectedDateMillis
                            val calendar = Calendar.getInstance()
                            if (dateSelectedMillis != null) {
                                calendar.timeInMillis = dateSelectedMillis
                            }
                            val selectedDate = calendar.time
                            val selectedTimestamp = Timestamp(selectedDate)
                            applyFilters(posts, selectedCategory, if (dateSelectedMillis != null) selectedTimestamp else null)
                        }
                    ) {
                        Text(text = "Confirm", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
