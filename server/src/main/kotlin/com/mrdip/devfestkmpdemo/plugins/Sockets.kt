package com.mrdip.devfestkmpdemo.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import java.time.Duration
import kotlin.time.toKotlinDuration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15).toKotlinDuration()
        timeout = Duration.ofSeconds(15).toKotlinDuration()
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}