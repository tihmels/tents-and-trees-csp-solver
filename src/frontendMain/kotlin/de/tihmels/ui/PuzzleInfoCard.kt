package de.tihmels.ui

import de.tihmels.TentsAndTrees
import io.kvision.core.Container

fun Container.tentData(puzzle: TentsAndTrees?) {

    puzzle?.let {
        sidebarCard("General Data", true) {

            flexBetween("Dimensions") {
                content = "${it.rows} x ${it.cols}"
            }

            flexBetween("Trees") {
                content = it.trees.count().toString()
            }

            flexBetween("Variables") {
                content = it.variables.count().toString()
            }

        }
    }

}
