package de.tihmels.csp.heuristic.variable.compound

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.annotation.HasName
import de.tihmels.csp.constraint.Constraint
import de.tihmels.csp.heuristic.variable.ISelectVariableHeuristic
import de.tihmels.csp.heuristic.variable.MinimumRemainingValuesHeuristic
import de.tihmels.csp.heuristic.variable.MostConstrainingVariableHeuristic

@HasName("Most Constraining Tie Breaker")
class MostConstrainingVariableTieBreakerHeuristic : ISelectVariableHeuristic {

    private val minimumRemainingValuesHeuristic: MinimumRemainingValuesHeuristic = MinimumRemainingValuesHeuristic()
    private val mostConstrainingVariableHeuristic: MostConstrainingVariableHeuristic =
        MostConstrainingVariableHeuristic()

    override fun selectVariable(
        unassignedVariables: List<Location>,
        domains: Map<Location, List<Domain>>,
        constraints: Map<Location, List<Constraint>>
    ): Location {
        return if (unassignedVariables.stream().mapToInt { i -> domains[i]?.size!! }.distinct()
                .count() == 1L
        ) {
            mostConstrainingVariableHeuristic.selectVariable(unassignedVariables, domains, constraints)
        } else {
            minimumRemainingValuesHeuristic.selectVariable(unassignedVariables, domains, constraints)
        }
    }
}