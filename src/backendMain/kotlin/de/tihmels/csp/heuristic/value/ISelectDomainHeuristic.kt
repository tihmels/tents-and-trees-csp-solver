package de.tihmels.csp.heuristic.value

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.constraint.Constraint

interface ISelectDomainHeuristic {
    fun chooseDomains(
        variable: Location,
        domains: Map<Location, List<Domain>>,
        assignment: Map<Location, Domain>,
        constraints: Map<Location, List<Constraint>>
    ): List<Domain>
}