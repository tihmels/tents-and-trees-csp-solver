package de.tihmels.csp.heuristic.value

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.constraint.Constraint
import de.tihmels.annotation.HasName
import java.util.function.Function
import java.util.stream.Collectors

@HasName("Least Constraining Value")
class LeastConstrainingValueOrderHeuristic : ISelectDomainHeuristic {

    override fun chooseDomains(
        variable: Location,
        domains: Map<Location, List<Domain>>,
        assignment: Map<Location, Domain>,
        constraints: Map<Location, List<Constraint>>
    ): List<Domain> {

        val domainValueRuledOutCounter: MutableMap<Domain, Int> = domains[variable]!!.stream().collect(
            Collectors.toMap(
                Function.identity(), { 0 })
        )

        val affectedUnassignedVariables: List<Location> = constraints[variable]!!.stream()
            .flatMap { con: Constraint -> con.variables.stream() }
            .filter { loc -> !assignment.containsKey(loc) }
            .filter { i -> i != variable }
            .distinct()
            .collect(Collectors.toList())

        // for each domain of the variable to assign..
        for (domain in domainValueRuledOutCounter.keys) {
            // create a shallow copy of assignments which we can add the domain
            val localAssignment = HashMap(assignment)
            localAssignment[variable] = domain

            // then for each affected variable get all possible domains...
            for (affectedVariable in affectedUnassignedVariables) {
                val affectedVariableDomains: List<Domain> = domains[affectedVariable]!!
                val localAssignmentPlusAffected = HashMap(localAssignment)

                // and check each for consistency issues
                for (affectedDomainValue in affectedVariableDomains) {
                    localAssignmentPlusAffected[affectedVariable] = affectedDomainValue
                    if (!isConsistent(affectedVariable, localAssignmentPlusAffected, constraints)) {
                        domainValueRuledOutCounter.merge(domain, 1) { a: Int?, b: Int? ->
                            Integer.sum(
                                a!!, b!!
                            )
                        }
                    }
                }
            }
        }
        return domainValueRuledOutCounter.entries.stream()
            .sorted(java.util.Map.Entry.comparingByValue())
            .map { i -> i.key }
            .collect(Collectors.toList())
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