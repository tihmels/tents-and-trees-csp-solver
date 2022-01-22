package de.tihmels

fun messageHandler(message: SMessageType) = when (message) {
    is SMessageType.PuzzleUpdate -> handleMessage(message)
    is SMessageType.AssignmentUpdate -> handleMessage(message)
    is SMessageType.BacktrackingUpdate -> handleMessage(message)
    is SMessageType.ConfigurationDataUpdate -> handleMessage(message)
    is SMessageType.AssignmentStateUpdate -> handleMessage(message)
}

private fun handleMessage(msg: SMessageType.AssignmentStateUpdate) {
    PuzzleStateService.updateAssignmentState(msg.assignment, msg.statistics)
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

