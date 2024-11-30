package com.mrdip.devfestkmpdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels { AppViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val message = viewModel.broadcastMessages.collectAsStateWithLifecycle()
            App(message = message.value)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun superDispatchKeyEvent(event: KeyEvent): Boolean {
        return when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                when (event.action) {
                    KeyEvent.ACTION_DOWN -> {
                        // Handle volume up press
                        viewModel.sendCommand(
                            commandMessage = CommandMessage(
                                command = Command.NEXT,
                                sourceDevice = DeviceType.ANDROID,
                                targetDevice = DeviceType.DESKTOP
                            )
                        )
                        true
                    }
                    KeyEvent.ACTION_UP -> {
                        // Handle volume up release
                        true
                    }
                    else -> false
                }
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                when (event.action) {
                    KeyEvent.ACTION_DOWN -> {
                        // Handle volume down press
                        viewModel.sendCommand(
                            commandMessage = CommandMessage(
                                command = Command.PREVIOUS,
                                sourceDevice = DeviceType.ANDROID,
                                targetDevice = DeviceType.DESKTOP
                            )
                        )
                        true
                    }
                    KeyEvent.ACTION_UP -> {
                        // Handle volume down release
                        true
                    }
                    else -> false
                }
            }
            else -> super.dispatchKeyEvent(event)
        }
    }
}