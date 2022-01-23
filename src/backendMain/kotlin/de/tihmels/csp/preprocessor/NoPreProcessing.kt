package de.tihmels.csp.preprocessor

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.csp.constraint.Constraint
import de.tihmels.annotation.HasName

@HasName("None")
class NoPreProcessing : IPreProcessor {

    override fun process(
        variables: List<Location>,
        domains: Map<Location, MutableList<Domain>>,
        constraints: Map<Location, List<Constraint>>
    ) {
    }

}