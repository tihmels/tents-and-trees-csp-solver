package de.tihmels

import de.tihmels.ConnectionState.DISCONNECTED
import de.tihmels.ConnectionState.ESTABLISHING
import de.tihmels.ws.WebsocketService
import io.kvision.state.ObservableValue
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

enum class ConnectionState {
    ESTABLISHING, CONNECTED, DISCONNECTED
}

class WebsocketHandler(
    private val messageHandler: (SMessage) -> Unit
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    internal val connectionState = ObservableValue(DISCONNECTED)

    private val websocketService: WebsocketService = WebsocketService()

    private val outgoingChannel: Channel<CMessage> = Channel()
    private val maxRetries = Constants.Websocket.maxReconnectTries

    fun send(msg: CMessage) = launch { outgoingChannel.send(msg) }

    fun connect() = GlobalScope.launch {
        var tries = 0

        while (tries < maxRetries) {

            if (connectionState.getState() != ESTABLISHING) connectionState.value = ESTABLISHING

            websocketService.socketConnection { output, input ->

                tries = 0
                connectionState.value = ConnectionState.CONNECTED

                connectChannels(output, input)
            }

            tries++
            delay(2000)
        }

        connectionState.value = DISCONNECTED
    }

    private suspend fun connectChannels(
        output: SendChannel<CMessage>,
        input: ReceiveChannel<SMessage>
    ) {
        coroutineScope {
            launch {
                for (msg in outgoingChannel) {
                    output.send(msg)
                }
            }
            launch {
                for (msg in input) {
                    messageHandler.invoke(msg)
                }
            }
        }
    }
}