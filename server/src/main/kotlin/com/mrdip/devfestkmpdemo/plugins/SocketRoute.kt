package com.mrdip.devfestkmpdemo.plugins

import com.mrdip.devfestkmpdemo.CommandMessage
import com.mrdip.devfestkmpdemo.DeviceType
import com.mrdip.devfestkmpdemo.PresentationRemoteServer
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

/**
 * Configures WebSocket routes for the presentation remote server.
 *
 * @param remoteServer The server managing device connections
 */
fun Route.socketRoutes(remoteServer: PresentationRemoteServer) {
    route("/connect") {
        webSocket {
            val deviceType = call.parameters["deviceType"]?.let {
                try {
                    DeviceType.valueOf(it.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            } ?: run {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Device type is required"))
                return@webSocket
            }

            if (remoteServer.addSession(this, deviceType) == null) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Device type already connected"))
                return@webSocket
            }

            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val receivedMessage = frame.readText()
                        val command = Json.decodeFromString<CommandMessage>(receivedMessage)
                        remoteServer.broadcastCommand(command)
                    }
                }
            } finally {
                remoteServer.removeSession(deviceType)
            }
        }
    }
}