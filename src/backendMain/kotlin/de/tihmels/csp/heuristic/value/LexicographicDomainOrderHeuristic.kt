package de.tihmels.csp.heuristic.value

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.constraint.Constraint
import de.tihmels.annotation.HasName

@HasName("Lexicographic Value")
class LexicographicDomainOrderHeuristic : ISelectDomainHeuristic {
    override fun chooseDomains(
        variable: Location,
        domains: Map<Location, List<Domain>>,
        assignment: Map<Location, Domain>,
        constraints: Map<Location, List<Constraint>>
    ): List<Domain> = domains[variable] ?: emptyList()

}