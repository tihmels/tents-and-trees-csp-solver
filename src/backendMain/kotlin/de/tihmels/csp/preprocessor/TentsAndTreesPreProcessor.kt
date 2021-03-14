package de.tihmels.csp.preprocessor

import de.tihmels.Domain
import de.tihmels.Location
import de.tihmels.TentsAndTrees
import de.tihmels.annotation.HasName
import de.tihmels.csp.*
import de.tihmels.csp.constraint.Constraint
import de.tihmels.csp.constraint.TentsPerLineConstraint
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors

@HasName("Custom Preprocessor")
class TentsAndTreesPreProcessor : IPreProcessor {

    private var trees: List<Location>? = null

    override fun setup(puzzle: TentsAndTrees) {
        super.setup(puzzle)
        trees = puzzle.trees
    }

    override fun process(
        variables: List<Location>,
        domains: Map<Location, MutableList<Domain>>,
        constraints: Map<Location, List<Constraint>>
    ) {
        val tentsPerLineConstraints: List<TentsPerLineConstraint> = getTentsPerLineConstraints(constraints)

        // remove tents from domain if zero tents can be placed
        restrictVariableDomainsOfZeroTentLines(tentsPerLineConstraints, domains)

        // if for a tree only one option to place a tent exists, then this variable has to be a tent
        checkIfTreeHasOnlyOneTentOption(variables, domains)

        // remove empty value when number of tents equals required tents
        restrictDomainValuesIfNumberOfTentsEqualsRequired(domains, tentsPerLineConstraints)
    }

    private fun restrictDomainValuesIfNumberOfTentsEqualsRequired(
        domains: Map<Location, MutableList<Domain>>,
        tentsPerLineConstraints: List<TentsPerLineConstraint>
    ) {
        for (constraint in tentsPerLineConstraints) {

            val locations: List<Location> = constraint.line
            val possibleTentsInLine = locations.stream()
                .filter { i: Location -> domains[i]?.stream()?.anyMatch(Domain::isTent)!! }.count().toInt()

            if (possibleTentsInLine == constraint.tents) {
                domains.entries.stream()
                    .filter { i: Map.Entry<Location, List<Domain>> -> locations.contains(i.key) }
                    .filter { i: Map.Entry<Location, List<Domain>> ->
                        i.value.stream().anyMatch(Domain::isTent)
                    }
                    .map { it.value }
                    .forEach { d -> d.removeIf(Predicate.not(Domain::isTent)) }
            }
        }
    }

    private fun checkIfTreeHasOnlyOneTentOption(
        variables: List<Location>,
        domains: Map<Location, MutableList<Domain>>
    ) {
        for (tree in trees!!) {
            val surroundingLocations: List<Location> = tree.getSurroundingLocations(variables, false)
            val surroundingLocationsWithDomain = surroundingLocations.stream()
                .collect(Collectors.toMap(Function.identity(), { k -> domains[k] }))
            var tentOptionsCounter = 0
            var tentToTree: Domain? = null

            for ((key, value) in surroundingLocationsWithDomain) {
                val positionToTree: PositionToTree? = PositionToTree.getRelativePositionToTree(key, tree)
                if (positionToTree != null && value?.contains(positionToTree.tile) == true) {
                    tentOptionsCounter++
                    tentToTree = positionToTree.tile
                }
            }
            if (tentOptionsCounter == 1) {
                when (tentToTree) {
                    Domain.TENT_TO_TOP -> surroundingLocationsWithDomain[tree.locationBelow]?.removeIf { t: Domain -> t !== Domain.TENT_TO_TOP }
                    Domain.TENT_TO_RIGHT -> surroundingLocationsWithDomain[tree.locationLeft]?.removeIf { t: Domain -> t !== Domain.TENT_TO_RIGHT }
                    Domain.TENT_TO_BOTTOM -> surroundingLocationsWithDomain[tree.locationAbove]?.removeIf { t: Domain -> t !== Domain.TENT_TO_BOTTOM }
                    Domain.TENT_TO_LEFT -> surroundingLocationsWithDomain[tree.locationRight]?.removeIf { t: Domain -> t !== Domain.TENT_TO_LEFT }
                    else -> {
                    }
                }
            }
        }
    }

    private fun restrictVariableDomainsOfZeroTentLines(
        tentsPerLineConstraints: List<TentsPerLineConstraint>,
        domains: Map<Location, MutableList<Domain>>
    ) {
        tentsPerLineConstraints.stream()
            .filter { c: TentsPerLineConstraint -> c.tents == 0 }
            .forEach { constraint: TentsPerLineConstraint ->
                val locations: List<Location> = constraint.line
                locations.forEach(Consumer { location: Location -> domains[location]?.removeIf(Domain::isTent) })
            }
    }

    private fun getTentsPerLineConstraints(constraints: Map<Location, List<Constraint>>): List<TentsPerLineConstraint> {
        return constraints.entries.stream()
            .flatMap { c -> c.value.stream() }
            .filter { c -> c is TentsPerLineConstraint }
            .map { c -> c as TentsPerLineConstraint }
            .collect(Collectors.toList())
    }

}