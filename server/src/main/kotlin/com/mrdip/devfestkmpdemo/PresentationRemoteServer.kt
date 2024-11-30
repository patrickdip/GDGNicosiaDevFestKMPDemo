package com.mrdip.devfestkmpdemo

import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicReference

/**
 * Server-side manager for handling WebSocket connections between devices in a presentation remote system.
 * Ensures only one connection per device type and manages connection state broadcasting.
 */
class PresentationRemoteServer {
    private val androidSession = AtomicReference<WebSocketSession?>(null)
    private val desktopSession = AtomicReference<WebSocketSession?>(null)

    /**
     * Adds a new device session to the server.
     *
     * @param session The WebSocket session to add
     * @param deviceType The type of device connecting
     * @return The added session, or null if a session for this device type already exists
     */
    suspend fun addSession(session: WebSocketSession, deviceType: DeviceType): WebSocketSession? {
        val sessionRef = getSessionReference(deviceType)
        if (!sessionRef.compareAndSet(null, session)) {
            return null // Reject if a session is already set
        }

        broadcastConnectionState(deviceType, true)
        return sessionRef.get()
    }

    /**
     * Removes a device session from the server.
     *
     * @param deviceType The type of device to disconnect
     */
    suspend fun removeSession(deviceType: DeviceType) {
        val sessionRef = getSessionReference(deviceType)
        sessionRef.set(null)
        broadcastConnectionState(deviceType, false)
    }

    /**
     * Broadcasts the connection state to the other device.
     *
     * @param deviceType The type of device that triggered the state change
     * @param isConnected Whether the device is connecting or disconnecting
     */
    private suspend fun broadcastConnectionState(deviceType: DeviceType, isConnected: Boolean) {
        val (targetDevice, sourceDevice) = when (deviceType) {
            DeviceType.ANDROID -> DeviceType.DESKTOP to DeviceType.ANDROID
            DeviceType.DESKTOP -> DeviceType.ANDROID to DeviceType.DESKTOP
        }

        val sourceConnectionState = if (getSessionReference(sourceDevice).get() != null)
            ConnectionState.CONNECTED
        else
            ConnectionState.DISCONNECTED

        val connectionInfo = ConnectionStateInfo(
            targetState = if (isConnected) ConnectionState.CONNECTED else ConnectionState.DISCONNECTED,
            sourceState = sourceConnectionState
        )

        broadcast(
            ConnectionStateBroadcastMessage(connectionStateInfo = connectionInfo),
            targetDevice
        )
    }

    /**
     * Broadcasts the connection state or a command to a specific device.
     *
     * @param message The message to broadcast
     * @param deviceType The target device to receive the message
     */
    private suspend fun broadcast(message: BroadcastMessage, deviceType: DeviceType) {
        val session = getSessionReference(deviceType).get()
        val jsonMessage = Json.encodeToString(message)
        session?.send(Frame.Text(jsonMessage))
    }

    /**
     * Broadcasts a command to a specific device.
     *
     * @param message The command message to broadcast
     */
    suspend fun broadcastCommand(message: CommandMessage) {
        message.targetDevice?.let { targetDevice ->
            broadcast(CommandBroadcastMessage(commandMessage = message), targetDevice)
        }
    }

    /**
     * Gets the appropriate session reference based on device type.
     *
     * @param deviceType The device type to get the session for
     * @return An atomic reference to the WebSocket session for the given device type
     */
    private fun getSessionReference(deviceType: DeviceType) =
        when (deviceType) {
            DeviceType.ANDROID -> androidSession
            DeviceType.DESKTOP -> desktopSession
        }
}