package de.tihmels.ws

import de.tihmels.CMessage
import de.tihmels.SMessage
import io.kvision.annotations.KVService
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

@KVService
interface IWebsocketService {

    suspend fun socketConnection(input: ReceiveChannel<CMessage>, output: SendChannel<SMessage>)

}