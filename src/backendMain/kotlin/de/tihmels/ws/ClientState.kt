package de.tihmels.ws

import de.tihmels.*
import de.tihmels.csp.CSP
import de.tihmels.csp.PuzzleCSPMapper
import de.tihmels.server.ConfigurationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

class ClientState(
    var activePuzzle: TentsAndTrees,
    private val output: SendChannel<SMessage>
) {

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    var configuration: Configuration = Configuration()
    var csp: CSP? = null

    var paused = false

    private var job: Job? = null

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

    suspend fun startBacktracking() {

        csp?.let { csp ->

            val flow = csp.backtrackingSearch()

            job = flow
                .onCompletion {
                    output.send(SMessage(SMessageType.BacktrackingUpdate(BacktrackingState.STOPPED)))
                }
                .map { assignmentToLocationAndValue(it) }
                .onEach {
                    output.send(SMessage(SMessageType.AssignmentUpdate(Assignment(it))))
                }
                .launchIn(scope)

        }

    }

    fun pauseBacktracking() {
        paused = !paused
    }

    suspend fun stopBacktracking() {

        job?.let {
            if (it.isActive) it.cancelAndJoin()
            job = null
        }

    }

    suspend fun setNewPuzzle(puzzle: TentsAndTrees) {
        stopBacktracking()

        activePuzzle = puzzle
    }

    fun updateConfiguration(configuration: Configuration) {
        this.configuration = configuration
        csp?.applyConfiguration(ConfigurationService.toCSPConfiguration(configuration))
    }

    private fun assignmentToLocationAndValue(assignment: Map<Location, Domain>): List<LocationAndValue> =
        assignment.entries.map { e -> LocationAndValue(e.key, e.value) }


}
