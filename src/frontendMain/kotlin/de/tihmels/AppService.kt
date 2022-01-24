package de.tihmels

object AppService {

    val websocketHandler = WebsocketHandler {
        messageHandler(it.messageType)
    }

    val connectionState = websocketHandler.connectionState

    fun connectToServer() = websocketHandler.connect()

    fun setBacktracking(backtracking: BacktrackingState) =
        websocketHandler.send(CMessage(CMessageType.SetBacktrackingState(backtracking)))

    fun getPuzzle() = websocketHandler.send(CMessage(CMessageType.GetPuzzle))

    fun getConfigurationData() = websocketHandler.send(CMessage(CMessageType.FetchConfigurationData))

}

