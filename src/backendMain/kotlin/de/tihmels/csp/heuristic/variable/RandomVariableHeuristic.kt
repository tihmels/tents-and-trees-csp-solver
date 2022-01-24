package de.tihmels.csp.heuristic.variable

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.annotation.HasName
import de.tihmels.csp.constraint.Constraint

@HasName("Random Variable")
class RandomVariableHeuristic : ISelectVariableHeuristic {

    override fun selectVariable(
        unassignedVariables: List<Location>,
        domains: Map<Location, List<Domain>>,
        constraints: Map<Location, List<Constraint>>
    ): Location = unassignedVariables.random()

}