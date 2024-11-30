package com.mrdip.devfestkmpdemo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(message: BroadcastMessage) {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            when(message){
                is CommandBroadcastMessage -> Unit
                is ConnectionStateBroadcastMessage -> {
                    Text(text = "Target state: ${message.connectionStateInfo.targetState.name}")
                }
                EmptyBroadcastMessage -> {
                    Text(text = "empty message")
                }
            }
        }
    }
}