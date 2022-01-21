package de.tihmels.ws


import de.tihmels.BacktrackingState
import de.tihmels.CMessageType
import de.tihmels.Logging
import de.tihmels.SMessageType.*
import de.tihmels.logger
import de.tihmels.misc.RandomSupplier
import de.tihmels.server.ConfigurationService
import de.tihmels.server.PuzzleLoader

class ClientHandler(private val client: Client) : Logging {

    private val randomPuzzleSupplier = RandomSupplier(PuzzleLoader.puzzles)
    private val clientState = ClientState(randomPuzzleSupplier.get(), client.output)

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
    }

    private suspend fun handleMessage(msg: CMessageType.GetPuzzle) {

        val puzzle = randomPuzzleSupplier.get()

        clientState.setNewPuzzle(puzzle)

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
        clientState.updateConfiguration(msg.configuration)
    }

    private suspend fun handleMessage(msg: CMessageType.SetBacktracking) = when (msg.backtracking) {
        BacktrackingState.RUNNING -> startBacktracking()
        BacktrackingState.PAUSED -> pauseBacktracking()
        BacktrackingState.STOPPED -> stopBacktracking()
    }

    private suspend fun startBacktracking() {

        clientState.setupCSP()
        clientState.startBacktracking()

        client.send(BacktrackingUpdate(BacktrackingState.RUNNING))
    }

    private suspend fun pauseBacktracking() {
        clientState.pauseBacktracking()

        client.send(BacktrackingUpdate(BacktrackingState.PAUSED))
    }

    private suspend fun stopBacktracking() {
        clientState.stopBacktracking()

        client.send(BacktrackingUpdate(BacktrackingState.STOPPED))
    }
}