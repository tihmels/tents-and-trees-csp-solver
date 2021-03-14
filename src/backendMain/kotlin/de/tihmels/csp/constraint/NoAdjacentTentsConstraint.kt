package de.tihmels.csp.constraint

import de.tihmels.Domain
import de.tihmels.Location

class NoAdjacentTentsConstraint(private val area: List<Location>) : Constraint(area) {

    override fun satisfied(assignment: Map<Location, Domain>): Boolean {
        val tents = area.stream()
            .filter(assignment::containsKey)
            .map(assignment::getValue)
            .filter(Domain::isTent)
            .count()

        return tents <= 1
    }


}