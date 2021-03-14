package de.tihmels.csp.heuristic.variable

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.constraint.Constraint
import de.tihmels.annotation.HasName
import java.util.*

@HasName("Minimum Remaining Values")
class MinimumRemainingValuesHeuristic : ISelectVariableHeuristic {

    override fun selectVariable(
        unassignedVariables: List<Location>,
        domains: Map<Location, List<Domain>>,
        constraints: Map<Location, List<Constraint>>
    ): Location {
        return unassignedVariables.stream()
            .min(Comparator.comparingInt { o -> domains[o]!!.size })
            .orElse(unassignedVariables.stream().findFirst().get())
    }
}