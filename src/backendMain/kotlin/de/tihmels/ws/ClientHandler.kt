package de.tihmels.ws


import de.tihmels.*
import de.tihmels.SMessageType.*
import de.tihmels.server.ConfigurationService
import de.tihmels.server.PuzzleProvider

class ClientHandler(private val client: Client): Logging {

    private val state = ClientState()

    private val log = logger()

    suspend fun start() {

        for (msg in client.input) {
            log.info("Incoming Message: ${msg.messageType.javaClass.simpleName}")

            handle(msg.messageType)
        }

    }

    private suspend fun handle(msg: CMessageType) = when (msg) {
        is CMessageType.GetPuzzle -> handleMessage(msg)
        is CMessageType.GetConfigurationData -> handleMessage(msg)
        is CMessageType.SetBacktracking -> handleMessage(msg)
        is CMessageType.SetConfiguration -> handleMessage(msg)
        else -> throw IllegalStateException()
    }

    private suspend fun handleMessage(msg: CMessageType.GetPuzzle) {

        state.stopBacktracking()

        val puzzle: TentsAndTrees by lazy {
            var p = PuzzleProvider.getRandomPuzzle()
            if (state.activePuzzle.id == p.id) p = PuzzleProvider.getRandomPuzzle()
            p
        }

        state.activePuzzle = puzzle

        client.send(
            PuzzleUpdate(
                puzzle
            )
        )
    }

    private suspend fun handleMessage(msg: CMessageType.GetConfigurationData) {
        client.send(
            ConfigurationDataUpdate(
                ConfigurationService.toCData()
            )
        )
    }

    private fun handleMessage(msg: CMessageType.SetConfiguration) {
        state.updateConfiguration(msg.configuration)
    }

    private suspend fun handleMessage(msg: CMessageType.SetBacktracking) = when (msg.backtracking) {
        BacktrackingState.RUNNING -> startBacktracking()
        BacktrackingState.PAUSED -> pauseBacktracking()
        BacktrackingState.STOPPED -> stopBacktracking()
    }

    private suspend fun startBacktracking() {

        state.setupCSP()
        state.startBacktracking(client.output)

        client.send(BacktrackingUpdate(BacktrackingState.RUNNING))
    }

    private suspend fun pauseBacktracking() {
        state.pauseBacktracking()
        client.send(BacktrackingUpdate(BacktrackingState.PAUSED))
    }

    private suspend fun stopBacktracking() {
        state.stopBacktracking()
        client.send(BacktrackingUpdate(BacktrackingState.STOPPED))
    }
}