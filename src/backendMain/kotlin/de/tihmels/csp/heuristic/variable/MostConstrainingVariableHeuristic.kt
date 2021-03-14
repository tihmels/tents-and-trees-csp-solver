package de.tihmels.csp.heuristic.variable

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.constraint.Constraint
import de.tihmels.annotation.HasName
import java.util.*

@HasName("Most Constraining Variable")
class MostConstrainingVariableHeuristic : ISelectVariableHeuristic {
    override fun selectVariable(
        unassignedVariables: List<Location>,
        domains: Map<Location, List<Domain>>,
        constraints: Map<Location, List<Constraint>>
    ): Location {
        return unassignedVariables.stream()
            .max(Comparator.comparingLong { variable ->
                getConstraintCount(
                    unassignedVariables,
                    constraints[variable]!!
                )
            })
            .orElse(null)
    }

    private fun getConstraintCount(unassignedVariables: List<Location>, constraints: List<Constraint>): Long {
        return constraints.stream()
            .flatMap { con -> con.variables.stream() }
            .filter { o -> unassignedVariables.contains(o) }
            .distinct()
            .count()
    }
}