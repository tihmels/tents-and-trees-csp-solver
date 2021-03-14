package de.tihmels.csp.constraint

import de.tihmels.Domain
import de.tihmels.Location

class TentsPerLineConstraint(val line: List<Location>, val tents: Int) :
    Constraint(line) {

    override fun satisfied(assignment: Map<Location, Domain>): Boolean {
        val tentsInLine = line.stream()
            .filter(assignment::containsKey)
            .map(assignment::getValue)
            .filter(Domain::isTent)
            .count();

        return if (allLocationsInLineAssigned(assignment)) tentsInLine == tents.toLong() else tentsInLine <= tents
    }

    private fun allLocationsInLineAssigned(a: Map<Location, Domain>): Boolean {
        return a.keys.stream().filter(line::contains).count().toInt() == line.size
    }
}
