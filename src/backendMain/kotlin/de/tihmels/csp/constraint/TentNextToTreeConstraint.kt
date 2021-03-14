package de.tihmels.csp.constraint

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.PositionToTree
import java.util.stream.Collectors

class TentNextToTreeConstraint(private val tree: Location, private val surroundings: List<Location>) :
    Constraint(surroundings) {

    override fun satisfied(assignment: Map<Location, Domain>): Boolean {

        val relevantEntries: List<Map.Entry<Location, Domain>> = getRelevantEntries(assignment)

        var tentCounter = 0

        for ((location, tile) in relevantEntries) {
            if (PositionToTree.getRelativePositionToTree(location, tree)?.tile == tile) {
                tentCounter++
            }
        }

        return if (relevantEntries.size == surroundings.size) tentCounter == 1 else tentCounter <= 1
    }

    private fun getRelevantEntries(assignment: Map<Location, Domain>): List<Map.Entry<Location, Domain>> =
        assignment.entries.stream()
            .filter { surroundings.contains(it.key) }
            .collect(Collectors.toList())


}