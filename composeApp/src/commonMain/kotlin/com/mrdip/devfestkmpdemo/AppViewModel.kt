package com.mrdip.devfestkmpdemo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    private val connectionManager = ConnectionManager(getDeviceType())
    var counter by mutableStateOf(0)
        private set

    val broadcastMessages = connectionManager
        .getBroadcastMessages()
        .onStart { connectionManager.connect() }
        .onEach {
            counter++
        }
        .catch { error ->
            println(error.message)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            EmptyBroadcastMessage
        )

    fun sendCommand(commandMessage: CommandMessage){
        viewModelScope.launch {
            connectionManager.sendCommand(commandMessage)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AppViewModel()
            }
        }
    }
}