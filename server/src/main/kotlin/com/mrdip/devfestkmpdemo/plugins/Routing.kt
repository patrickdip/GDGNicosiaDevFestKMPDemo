package com.mrdip.devfestkmpdemo.plugins

import com.mrdip.devfestkmpdemo.PresentationRemoteServer
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting(remoteServer: PresentationRemoteServer) {
    routing {
        socketRoutes(remoteServer)
    }
}