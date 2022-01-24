package de.tihmels.csp.heuristic.value

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.constraint.Constraint
import de.tihmels.annotation.HasName

@HasName("Random Value")
class RandomValueOrderHeuristic : ISelectDomainHeuristic {
    override fun chooseDomains(
        variable: Location,
        domains: Map<Location, List<Domain>>,
        assignment: Map<Location, Domain>,
        constraints: Map<Location, List<Constraint>>
    ): List<Domain> {
        val list = (domains[variable]!!).toMutableList()
        list.shuffle()
        return list
    }

}