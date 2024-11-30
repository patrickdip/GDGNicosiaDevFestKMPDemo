package com.mrdip.devfestkmpdemo

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ConnectionManager(private val deviceType: DeviceType) {
    private val client = HttpClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json {
                classDiscriminator = "messageType"
            })
        }
    }

    private var session: WebSocketSession? = null

    suspend fun connect() {
        session = client.webSocketSession(urlString = getUrl(deviceType = deviceType))
    }

    suspend fun sendCommand(commandMessage: CommandMessage) {
        session?.outgoing?.send(
            Frame.Text(Json.encodeToString(commandMessage))
        )
    }

    fun getBroadcastMessages(): Flow<BroadcastMessage> {
        return flow {
            if (session == null) connect()
            val commands = session!!
                .incoming
                .consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .mapNotNull { Json.decodeFromString<BroadcastMessage>(it.readText()) }

            emitAll(commands)
        }
    }

    private fun getUrl(deviceType: DeviceType): String {
        return "ws://192.168.0.102:8087/connect?deviceType=${deviceType.name}"
    }

    suspend fun disconnect() {
        session?.close()
        session = null
    }
}