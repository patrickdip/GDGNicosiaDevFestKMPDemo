package com.mrdip.devfestkmpdemo

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.compose.viewModel

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GDG Nicosia DevFest KMP Demo",
    ) {
        val simulator = KeyboardSimulatorMacOS()
        val viewModel: AppViewModel = viewModel { AppViewModel() }
        val messages = viewModel.broadcastMessages.collectAsState()
        val counter = viewModel.counter

        Column {
            when(val message = messages.value){
                is CommandBroadcastMessage -> {
                    if (message.commandMessage.command == Command.NEXT){
                        simulator.simulateRightArrowKeyPress()
                    }else{
                        simulator.simulateLeftArrowKeyPress()
                    }
                }
                is ConnectionStateBroadcastMessage -> Unit
                EmptyBroadcastMessage -> Unit
            }
            App(message = messages.value)
        }
    }
}