package de.tihmels.ws

import de.tihmels.CMessage
import de.tihmels.Logging
import de.tihmels.SMessage
import de.tihmels.logger
import de.tihmels.ws.Connections.clients
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.util.concurrent.ConcurrentHashMap

object Connections {
    val clients: ConcurrentHashMap.KeySetView<Client, Boolean> = ConcurrentHashMap.newKeySet()
}

actual class WebsocketService : IWebsocketService, Logging {

    private val log = logger()

    override suspend fun socketConnection(input: ReceiveChannel<CMessage>, output: SendChannel<SMessage>) {
        val client = Client(input, output).also {
            clients.add(it)
            log.info("Client connected")
        }

        ClientHandler(client).start()

        clients.remove(client)
        log.info("Client disconnected")
    }

}