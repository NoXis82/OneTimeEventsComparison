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

    /**
     * Channel фактически имеют буфер (он может продолжить работу как только снова получит подписчика)
     * Channel предназначены для одного подписчика
     */

    private val navigationChannel = Channel<NavigationEvent>()
    val navigationEventChannelFlow = navigationChannel.receiveAsFlow()

    /**
     * SharedFlow по умолчанию не имеют буфера, но его можно условно задать с помощью например replay
     * иначе при сворачивании приложения мы теряем состояние
     * SharedFlow могут иметь несколько подписчиков
     */
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>(replay = 3)
    val navigationEventSharedFlow = _navigationEvent.asSharedFlow()

    /**
     * Но эти API не гарантируют доставку и обработку этих событий
     */

    var isLoggedIn by mutableStateOf(false)
        private set

    var state by mutableStateOf(LoginState())
        private set

    //Получается костыль чтобы работал back stack в naviagtion
    fun onNavigatedToLogin() {
        state = state.copy(isLoggedIn = false)
    }

    fun login() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            println("State: ${state.isLoading}")

            delay(3000)

            //Channel
//            navigationChannel.send(NavigationEvent.NavigateToProfile)

            //SharedFlow
//            _navigationEvent.emit(NavigationEvent.NavigateToProfile)

            //Не будет работать back stack для экранов
            state = state.copy(isLoading = false, isLoggedIn = true)
            println("State: ${state.isLoading}")

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
