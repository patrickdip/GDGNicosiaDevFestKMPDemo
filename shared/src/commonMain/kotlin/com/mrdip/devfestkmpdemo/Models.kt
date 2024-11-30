package com.mrdip.devfestkmpdemo

import kotlinx.serialization.Serializable

@Serializable
enum class DeviceType {
    ANDROID, DESKTOP
}

@Serializable
enum class Command {
    NEXT, PREVIOUS
}

@Serializable
data class CommandMessage(
    val command: Command,
    val sourceDevice: DeviceType,
    val targetDevice: DeviceType? = null
)

enum class ConnectionState {
    CONNECTED, DISCONNECTED
}

@Serializable
data class ConnectionStateInfo(
    val targetState: ConnectionState = ConnectionState.DISCONNECTED, // other device
    val sourceState: ConnectionState = ConnectionState.DISCONNECTED // current device
)

@Serializable
sealed class BroadcastMessage {
    abstract val messageType: String
}

@Serializable
data class CommandBroadcastMessage(
    override val messageType: String = "command",
    val commandMessage: CommandMessage
) : BroadcastMessage()

@Serializable
data class ConnectionStateBroadcastMessage(
    override val messageType: String = "connection_state",
    val connectionStateInfo: ConnectionStateInfo
) : BroadcastMessage()

data object EmptyBroadcastMessage: BroadcastMessage() {
    override val messageType: String
        get() = "NO_MESSAGE"
}