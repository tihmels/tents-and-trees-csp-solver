package de.tihmels

fun messageHandler(message: SMessageType) = when (message) {
    is SMessageType.PuzzleUpdate -> handleMessage(message)
    is SMessageType.BacktrackingStateUpdate -> handleMessage(message)
    is SMessageType.ConfigurationDataUpdate -> handleMessage(message)
    is SMessageType.AssignmentStateUpdate -> handleMessage(message)
}

private fun handleMessage(msg: SMessageType.AssignmentStateUpdate) {
    ClientStateService.updateAssignmentState(msg.assignment, msg.statistics)
}

private fun handleMessage(msg: SMessageType.PuzzleUpdate) {
    ClientStateService.updatePuzzle(msg.puzzle)
}

private fun handleMessage(msg: SMessageType.BacktrackingStateUpdate) {
    ClientStateService.updateBacktrackingState(msg.state)
}

private fun handleMessage(msg: SMessageType.ConfigurationDataUpdate) {
    ConfigurationService.setConfigurationData(msg.configurationData)
}

