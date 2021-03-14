package de.tihmels.csp

import de.tihmels.*
import de.tihmels.csp.constraint.NoAdjacentTentsConstraint
import de.tihmels.csp.constraint.TentNextToTreeConstraint
import de.tihmels.csp.constraint.TentsPerLineConstraint
import java.util.stream.Collectors

class PuzzleCSPMapper(
    private val puzzle: TentsAndTrees
) {

    fun createCSP(configuration: CSPConfiguration): CSP {

        val domains = puzzle.variables.associateBy({ it },
            {
                mutableListOf<Domain>().apply { add(Domain.GRASS) }
            }
        )

        addTentsToDomains(domains)

        val csp = CSP(
            puzzle.variables,
            domains,
            configuration
        )

        addTentsPerRowConstraints(csp)
        addTentsPerColumnConstraints(csp)
        addNoAdjacentTentsConstraints(csp)
        addTentsNextToTreeConstraints(csp)

        return csp
    }

    private fun addTentsToDomains(domains: Map<Location, MutableList<Domain>>) {
        for (tree in puzzle.trees) {
            domains[tree.locationAbove]?.add(Domain.TENT_TO_BOTTOM)
            domains[tree.locationRight]?.add(Domain.TENT_TO_LEFT)
            domains[tree.locationLeft]?.add(Domain.TENT_TO_RIGHT)
            domains[tree.locationBelow]?.add(Domain.TENT_TO_TOP)
        }
    }

    private fun addTentsPerColumnConstraints(csp: CSP) {
        require(puzzle.cols != null)

        for (col in 0 until puzzle.cols) {
            val variablesInColumn = puzzle.variables.stream()
                .filter { l -> l.column == col }
                .collect(Collectors.toList())

            csp.addConstraint(TentsPerLineConstraint(variablesInColumn, puzzle.tentsPerCol[col]))
        }
    }

    private fun addTentsPerRowConstraints(csp: CSP) {
        require(puzzle.rows != null)

        for (row in 0 until puzzle.rows) {
            val variablesInRow = puzzle.variables.stream()
                .filter { l -> l.row == row }
                .collect(Collectors.toList())

            csp.addConstraint(TentsPerLineConstraint(variablesInRow, puzzle.tentsPerRow[row]))
        }
    }

    private fun addNoAdjacentTentsConstraints(csp: CSP) {
        require(puzzle.rows != null && puzzle.cols != null)

        for (row in 0 until puzzle.rows) {
            for (col in 0 until puzzle.cols) {
                val location = Location(row, col)
                csp.addConstraint(NoAdjacentTentsConstraint(location.getTentQuadrant(puzzle.variables)))
            }
        }
    }

    private fun addTentsNextToTreeConstraints(csp: CSP) {
        for (tree in puzzle.trees) {
            csp.addConstraint(TentNextToTreeConstraint(tree, tree.getSurroundingLocations(puzzle.variables, false)))
        }
    }

}