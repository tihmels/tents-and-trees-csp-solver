package de.tihmels.csp

import de.tihmels.TentsAndTrees
import de.tihmels.csp.heuristic.value.ISelectDomainHeuristic
import de.tihmels.csp.heuristic.variable.ISelectVariableHeuristic
import de.tihmels.csp.preprocessor.IPreProcessor
import de.tihmels.csp.propagation.IConstraintPropagation

data class CSPConfiguration(
    val preProcessingStrategy: IPreProcessor,
    val variableSelectionStrategy: ISelectVariableHeuristic,
    val domainSelectionStrategy: ISelectDomainHeuristic,
    val constraintPropagationStrategy: IConstraintPropagation,
    val speed: Int
) {

    fun setup(puzzle: TentsAndTrees) {
        preProcessingStrategy.setup(puzzle)
    }

}