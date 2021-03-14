package de.tihmels

object AppService {

    val websocketHandler = WebsocketHandler {
        this.messageHandler(it.messageType)
    }

    val connectionState = websocketHandler.connectionState

    private fun messageHandler(message: SMessageType) = when (message) {
        is SMessageType.PuzzleUpdate -> handleMessage(message)
        is SMessageType.AssignmentUpdate -> handleMessage(message)
        is SMessageType.BacktrackingUpdate -> handleMessage(message)
        is SMessageType.ConfigurationDataUpdate -> handleMessage(message)
    }

    private fun handleMessage(msg: SMessageType.PuzzleUpdate) {
        PuzzleStateService.updatePuzzle(msg.puzzle)
    }

    private fun handleMessage(msg: SMessageType.AssignmentUpdate) {
        PuzzleStateService.updateAssignment(msg.assignment)
    }

    private fun handleMessage(msg: SMessageType.BacktrackingUpdate) {
        PuzzleStateService.updateState(msg.state)
    }

    private fun handleMessage(msg: SMessageType.ConfigurationDataUpdate) {
        ConfigurationService.setConfigurationData(msg.configurationData)
    }

    fun connectToServer() = this.websocketHandler.connect()

    fun setBacktracking(backtracking: BacktrackingState) =
        websocketHandler.send(CMessage(CMessageType.SetBacktracking(backtracking)))

    fun getPuzzle() = websocketHandler.send(CMessage(CMessageType.GetPuzzle))

    fun getConfigurationData() = websocketHandler.send(CMessage(CMessageType.GetConfigurationData))

}

