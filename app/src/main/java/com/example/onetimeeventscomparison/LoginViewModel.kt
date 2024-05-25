package com.example.onetimeeventscomparison

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {

    private val navigationChannel = Channel<NavigationEvent>()
    val navigationEventChannelFlow = navigationChannel.receiveAsFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEventSharedFlow = _navigationEvent.asSharedFlow()

    var isLoggedIn by mutableStateOf(false)
        private set

    var state by mutableStateOf(LoginState())
        private set

    fun login() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(3000L)

            navigationChannel.send(NavigationEvent.NavigateToProfile)

            state = state.copy(isLoading = false)
        }
    }


}

sealed interface NavigationEvent {
    data object NavigateToProfile : NavigationEvent
}

data class LoginState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)
