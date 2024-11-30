package com.mrdip.devfestkmpdemo

import com.mrdip.devfestkmpdemo.plugins.configureRouting
import com.mrdip.devfestkmpdemo.plugins.configureSerialization
import com.mrdip.devfestkmpdemo.plugins.configureSockets
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8087, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val remoteServer = PresentationRemoteServer()
    configureSerialization()
    configureSockets()
    configureRouting(remoteServer)
}