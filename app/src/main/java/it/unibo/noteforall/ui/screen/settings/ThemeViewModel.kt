package it.unibo.noteforall.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.noteforall.data.repositories.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemeState(val theme: Theme)

class ThemeViewModel(
    private val repository: ThemeRepository
) : ViewModel() {
    val state = repository.theme.map { ThemeState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ThemeState(Theme.System)
    )

    fun changeTheme(theme: Theme) = viewModelScope.launch {
        repository.setTheme(theme)
    }

    fun deleteTheme() = viewModelScope.launch {
        repository.deleteTheme()
    }
}
