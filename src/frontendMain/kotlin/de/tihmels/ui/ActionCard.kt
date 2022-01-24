package de.tihmels.ui

import de.tihmels.AppService
import de.tihmels.BacktrackingState
import de.tihmels.ConfigurationService
import de.tihmels.PuzzleStateService
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.html.button
import io.kvision.panel.flexPanel
import io.kvision.state.bind
import io.kvision.state.sub

fun Container.actions() {

    sidebarCard("Actions") {

        val backtrackingState = PuzzleStateService.puzzleState.sub { it.backtracking }

        flexPanel(justify = JustifyContent.SPACEEVENLY) {

            button("Start") {

                bind(backtrackingState) {
                    disabled = it == BacktrackingState.RUNNING
                }

                onClick {
                    //ConfigurationService.updateConfiguration()
                    AppService.setBacktracking(BacktrackingState.RUNNING)
                }

            }

            button("Stop", className = "btn-danger") {

                bind(backtrackingState) {
                    disabled = it != BacktrackingState.RUNNING
                }

                onClick {
                    AppService.setBacktracking(BacktrackingState.STOPPED)
                }
            }
        }

    }
}