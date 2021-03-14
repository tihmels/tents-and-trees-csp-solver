package de.tihmels.ws

import de.tihmels.CMessage
import de.tihmels.SMessage
import de.tihmels.SMessageType
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.util.*
import java.util.UUID.randomUUID

data class Client(val input: ReceiveChannel<CMessage>, val output: SendChannel<SMessage>) {

    val uuid: UUID = randomUUID()

    suspend fun send(msg: SMessageType) = output.send(SMessage(msg))

}