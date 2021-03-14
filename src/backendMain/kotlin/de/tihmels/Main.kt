package de.tihmels

import de.tihmels.ws.WebsocketServiceManager
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.websocket.*
import io.kvision.remote.applyRoutes
import io.kvision.remote.kvisionInit

fun Application.main() {
    install(Compression)
    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets)

    routing {
        applyRoutes(WebsocketServiceManager)
    }

    kvisionInit()
}
