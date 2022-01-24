package de.tihmels

import io.kvision.redux.RAction
import io.kvision.redux.createReduxStore

data class ClientState(
    val puzzle: TentsAndTrees? = null,
    val assignment: Assignment? = null,
    val backtracking: BacktrackingState? = null,
    val statistics: CSPStatistics? = null,
)

object ClientStateService {

    val puzzleState = createReduxStore(::gridReducer, ClientState())

    fun updatePuzzle(puzzle: TentsAndTrees) {
        this.puzzleState.dispatch(StateAction.UpdatePuzzle(puzzle))
    }

    fun updateBacktrackingState(state: BacktrackingState) {
        this.puzzleState.dispatch(StateAction.UpdateBacktracking(state))
    }

    fun updateAssignmentState(assignment: Assignment, statistics: CSPStatistics) {
        this.puzzleState.dispatch(StateAction.UpdateAssignmentState(assignment, statistics))
    }

    fun getCurrentProgressInPercent(): Float {
        val numberOfVariables = this.puzzleState.getState().puzzle?.variables?.size
        val numberOfAssignments = this.puzzleState.getState().assignment?.assignments?.size

        if (numberOfVariables != null && numberOfAssignments != null) {
            return (numberOfAssignments.toFloat() / numberOfVariables.toFloat()) * 100
        }

        return 0f
    }

    sealed class StateAction : RAction {
        data class UpdateBacktracking(val state: BacktrackingState) : StateAction()
        data class UpdatePuzzle(val puzzle: TentsAndTrees) : StateAction()
        class UpdateAssignmentState(val assignment: Assignment, val statistics: CSPStatistics) : StateAction()
    }

    private fun gridReducer(state: ClientState, action: StateAction): ClientState = when (action) {
        is StateAction.UpdatePuzzle -> {
            state.copy(
                puzzle = action.puzzle, assignment = Assignment(), statistics = CSPStatistics()
            )
        }
        is StateAction.UpdateBacktracking -> {
            state.copy(backtracking = action.state)
        }
        is StateAction.UpdateAssignmentState -> {
            state.copy(assignment = action.assignment, statistics = action.statistics)
        }
    }

}