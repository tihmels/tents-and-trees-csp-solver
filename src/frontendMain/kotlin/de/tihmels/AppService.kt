package de.tihmels

object AppService {

    val websocketHandler = WebsocketHandler {
        messageHandler(it.messageType)
    }

    val connectionState = websocketHandler.connectionState

    fun connectToServer() = websocketHandler.connect()

    fun setBacktracking(backtracking: BacktrackingState) =
        websocketHandler.send(CMessage(CMessageType.SetBacktrackingState(backtracking)))

    fun getPuzzle(id: Int = -1) = websocketHandler.send(CMessage(CMessageType.GetPuzzle(id)))

    fun getConfigurationData() = websocketHandler.send(CMessage(CMessageType.FetchConfigurationData))

}

