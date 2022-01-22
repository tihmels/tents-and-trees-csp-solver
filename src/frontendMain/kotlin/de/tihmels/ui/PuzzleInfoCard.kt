package de.tihmels.ui

import de.tihmels.PuzzleStateService
import de.tihmels.TentsAndTrees
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.state.bind
import io.kvision.state.sub

fun Container.tentData(puzzle: TentsAndTrees?) {

    puzzle?.let {
        sidebarCard("General", true) {

            flexBetweenBadge("Dimensions") {
                content = "${it.rows} x ${it.cols}"
            }

            flexBetweenBadge("Trees") {
                content = it.trees.count().toString()
            }

            flexBetweenBadge("Variables") {
                content = it.variables.count().toString()
            }

            val statisticsStore = PuzzleStateService.puzzleState.sub { it.statistics }

            div().bind(statisticsStore) {

                flexBetweenBadge("Steps") {
                    addCssClass("badge-info")
                    content = it.totalSteps.toString()
                }

                flexBetweenBadge("Errors") {
                    addCssClass("badge-info")
                    content = it.totalErrors.toString()
                }


            }


        }
    }

}
