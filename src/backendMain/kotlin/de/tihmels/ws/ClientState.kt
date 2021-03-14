package de.tihmels.ws

import de.tihmels.*
import de.tihmels.csp.CSP
import de.tihmels.csp.PuzzleCSPMapper
import de.tihmels.server.ConfigurationService
import de.tihmels.server.PuzzleProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel

class ClientState(
    var activePuzzle: TentsAndTrees = PuzzleProvider.getRandomPuzzle(),
    var configuration: Configuration = Configuration(),
    var job: Job? = null,
    var csp: CSP? = null
) {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    fun setupCSP() {

        val configuration = ConfigurationService.toCSPConfiguration(
            configuration
        )

        configuration.setup(activePuzzle)

        val csp = PuzzleCSPMapper(activePuzzle)
            .createCSP(configuration)

        csp.applyConfiguration(configuration)

        this.csp = csp
    }

    fun startBacktracking(channel: SendChannel<SMessage>) {
        job = scope.launch {
            csp!!.backtrackingSearch(channel)
        }

        job?.invokeOnCompletion {
            scope.launch {
                channel.send(SMessage(SMessageType.BacktrackingUpdate(BacktrackingState.STOPPED)))
            }
        }
    }

    fun pauseBacktracking() {

    }

    suspend fun stopBacktracking() {
        job?.cancelAndJoin()
        job = null
    }

    fun updateConfiguration(configuration: Configuration) {
        this.configuration = configuration

        csp?.applyConfiguration(ConfigurationService.toCSPConfiguration(configuration))
    }

}