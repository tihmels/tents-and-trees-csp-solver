package de.tihmels.csp.preprocessor

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.TentsAndTrees
import de.tihmels.csp.constraint.Constraint

interface IPreProcessor {

    fun process(
        variables: List<Location>,
        domains: Map<Location, MutableList<Domain>>,
        constraints: Map<Location, List<Constraint>>
    )

    fun setup(puzzle: TentsAndTrees) {}

}