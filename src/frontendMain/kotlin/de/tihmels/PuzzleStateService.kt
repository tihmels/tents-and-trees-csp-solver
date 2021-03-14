package de.tihmels

import io.kvision.redux.RAction
import io.kvision.redux.createReduxStore

data class PuzzleState(
    val puzzle: TentsAndTrees? = null,
    val assignment: Assignment? = null,
    val backtracking: BacktrackingState? = null
)

object PuzzleStateService {

    val puzzleState = createReduxStore(::gridReducer, PuzzleState())

    fun updatePuzzle(puzzle: TentsAndTrees) {
        this.puzzleState.dispatch(StateAction.UpdatePuzzle(puzzle))
    }

    fun updateAssignment(assignment: Assignment) {
        this.puzzleState.dispatch(StateAction.UpdateAssignment(assignment))
    }

    fun updateState(state: BacktrackingState) {
        this.puzzleState.dispatch(StateAction.UpdateBacktracking(state))
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
        data class UpdateAssignment(val assignment: Assignment) : StateAction()
    }

    private fun gridReducer(state: PuzzleState, action: StateAction): PuzzleState = when (action) {
        is StateAction.UpdatePuzzle -> {
            state.copy(
                puzzle = action.puzzle,
                assignment = Assignment()
            )
        }
        is StateAction.UpdateAssignment -> {
            state.copy(
                assignment = action.assignment
            )
        }
        is StateAction.UpdateBacktracking -> {
            state.copy(backtracking = action.state)
        }
    }

}