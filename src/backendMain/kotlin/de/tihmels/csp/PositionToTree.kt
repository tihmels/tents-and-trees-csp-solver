package de.tihmels.csp

import de.tihmels.Domain
import de.tihmels.Domain.*
import de.tihmels.Location

enum class PositionToTree(var tile: Domain) {
    ABOVE(TENT_TO_BOTTOM), LEFT(TENT_TO_RIGHT), RIGHT(TENT_TO_LEFT), BELOW(TENT_TO_TOP);

    companion object {
        fun getRelativePositionToTree(location: Location, tree: Location) =
            when {
                location.isRightTo(tree) -> {
                    RIGHT
                }
                location.isLeftTo(tree) -> {
                    LEFT
                }
                location.isBelow(tree) -> {
                    BELOW
                }
                location.isAbove(tree) -> {
                    ABOVE
                }
                else -> null
            }
    }
}
