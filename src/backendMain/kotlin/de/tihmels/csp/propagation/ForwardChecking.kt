package de.tihmels.csp.propagation

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.constraint.Constraint
import de.tihmels.annotation.HasName
import java.util.function.Predicate
import java.util.stream.Collectors

@HasName("Foward Checking")
open class ForwardChecking : IConstraintPropagation {

    override fun propagate(
        assignedVariable: Location,
        assignments: Map<Location, Domain>,
        domains: Map<Location, MutableList<Domain>>,
        constraints: Map<Location, List<Constraint>>
    ) {
        val affectedUnassignedVariables: List<Location> = constraints[assignedVariable]!!.stream()
            .flatMap { c -> c.variables.stream() }
            .filter(Predicate.not(assignments::containsKey))
            .distinct()
            .collect(Collectors.toList())

        for (variable in affectedUnassignedVariables) {
            val variableDomains: List<Domain> = domains[variable]!!

            val iterator: MutableIterator<Domain> = variableDomains.toMutableList().iterator()
            while (iterator.hasNext()) {
                val domain = iterator.next()
                val localAssignment = HashMap(assignments)
                localAssignment[variable] = domain
                if (!isConsistent(variable, localAssignment, constraints)) {
                    iterator.remove()
                }
            }
        }
    }

    private fun isConsistent(
        variable: Location,
        assignment: Map<Location, Domain>,
        constraints: Map<Location, List<Constraint>>
    ): Boolean {
        for (constraint in constraints[variable]!!) {
            if (!constraint.satisfied(assignment)) {
                return false
            }
        }
        return true
    }

}