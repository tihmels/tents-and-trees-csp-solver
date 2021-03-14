package de.tihmels.csp.propagation

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.constraint.Constraint

interface IConstraintPropagation {

    fun propagate(
        assignedVariable: Location,
        assignments: Map<Location, Domain>,
        domains: Map<Location, MutableList<Domain>>,
        constraints: Map<Location, List<Constraint>>
    )

}