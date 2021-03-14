package de.tihmels.csp.propagation

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.constraint.Constraint
import de.tihmels.csp.constraint.TentsPerLineConstraint
import de.tihmels.csp.getSurroundingLocations
import de.tihmels.annotation.HasName
import java.util.function.Predicate
import java.util.stream.Collectors

@HasName("Custom Forward Checking")
class TentsAndTreesForwardChecking :
    ForwardChecking() {

    override fun propagate(
        assignedVariable: Location,
        assignments: Map<Location, Domain>,
        domains: Map<Location, MutableList<Domain>>,
        constraints: Map<Location, List<Constraint>>
    ) {
        super.propagate(assignedVariable, assignments, domains, constraints)

        if (!assignments[assignedVariable]?.isTent!! && domains[assignedVariable]?.stream()
                ?.anyMatch(Domain::isTent)!!
        ) {
            val tentsPerLineConstraints: List<TentsPerLineConstraint> = constraints[assignedVariable]!!.stream()
                .filter { i -> i is TentsPerLineConstraint }
                .map { i -> i as TentsPerLineConstraint }
                .collect(Collectors.toList())

            // check all tents per line constraints
            for (constraint in tentsPerLineConstraints) {
                val assignedVariablesInLine: Map<Boolean, List<Location>> = constraint.variables.stream().collect(
                    Collectors.partitioningBy { key -> assignments.containsKey(key) }
                )
                val tentsAlreadyPresent = assignedVariablesInLine[java.lang.Boolean.TRUE]!!.stream()
                    .filter { i: Location? -> assignments[i]?.isTent!! }
                    .count()
                val possibleUnassignedTents = assignedVariablesInLine[java.lang.Boolean.FALSE]!!.stream()
                    .filter { i: Location? -> domains[i]?.stream()?.anyMatch(Domain::isTent)!! }
                    .count()

                // if the number of remaining POSSIBLE tents in line + the Tents Already Present matches exactly the required tents per line, then those HAVE TO BE a tent.
                if (possibleUnassignedTents + tentsAlreadyPresent == constraint.tents.toLong() && possibleUnassignedTents > 0) assignedVariablesInLine[java.lang.Boolean.FALSE]!!.stream()
                    .filter { v: Location? -> domains[v]?.stream()?.anyMatch(Domain::isTent)!! }
                    .forEach { i: Location? -> domains[i]!!.removeIf(Predicate.not(Domain::isTent)) }
            }
        } else if (assignments[assignedVariable]?.isTent!!) {

            val unassignedSurroundings = assignedVariable.getSurroundingLocations(domains.keys.toList(), true)
                .filter { !assignments.containsKey(it) }

            unassignedSurroundings
                .forEach {
                    domains[it]!!.removeIf(Domain::isTent)
                }
        }
    }
}