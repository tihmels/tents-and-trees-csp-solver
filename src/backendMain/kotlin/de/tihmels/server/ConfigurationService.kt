package de.tihmels.server

import de.tihmels.Configuration
import de.tihmels.ConfigurationData
import de.tihmels.annotation.HasName
import de.tihmels.csp.CSPConfiguration
import de.tihmels.csp.heuristic.value.ISelectDomainHeuristic
import de.tihmels.csp.heuristic.value.LeastConstrainingValueOrderHeuristic
import de.tihmels.csp.heuristic.value.LexicographicDomainOrderHeuristic
import de.tihmels.csp.heuristic.value.RandomValueOrderHeuristic
import de.tihmels.csp.heuristic.variable.ISelectVariableHeuristic
import de.tihmels.csp.heuristic.variable.MinimumRemainingValuesHeuristic
import de.tihmels.csp.heuristic.variable.MostConstrainingVariableHeuristic
import de.tihmels.csp.heuristic.variable.SelectFirstUnassignedVariableHeuristic
import de.tihmels.csp.heuristic.variable.compound.MostConstrainingVariableTieBreakerHeuristic
import de.tihmels.csp.preprocessor.IPreProcessor
import de.tihmels.csp.preprocessor.NoPreProcessing
import de.tihmels.csp.preprocessor.TentsAndTreesPreProcessor
import de.tihmels.csp.propagation.ForwardChecking
import de.tihmels.csp.propagation.IConstraintPropagation
import de.tihmels.csp.propagation.NoConstraintPropagation
import de.tihmels.csp.propagation.TentsAndTreesForwardChecking

object ConfigurationService {

    private val preProcessingStrategies: Map<Int, Class<out IPreProcessor>> =
        mapOf(
            0 to NoPreProcessing::class.java,
            1 to TentsAndTreesPreProcessor::class.java
        )

    private val variableSelectionStrategies: Map<Int, Class<out ISelectVariableHeuristic>> =
        mapOf(
            0 to SelectFirstUnassignedVariableHeuristic::class.java,
            1 to MinimumRemainingValuesHeuristic::class.java,
            2 to MostConstrainingVariableHeuristic::class.java,
            3 to MostConstrainingVariableTieBreakerHeuristic::class.java
        )

    private val domainSelectionStrategies: Map<Int, Class<out ISelectDomainHeuristic>> =
        mapOf(
            0 to LexicographicDomainOrderHeuristic::class.java,
            1 to RandomValueOrderHeuristic::class.java,
            2 to LeastConstrainingValueOrderHeuristic::class.java
        )

    private val constraintPropagationStrategies: Map<Int, Class<out IConstraintPropagation>> =
        mapOf(
            0 to NoConstraintPropagation::class.java,
            1 to ForwardChecking::class.java,
            2 to TentsAndTreesForwardChecking::class.java
        )

    private val speedRange = Pair(1, 10)

    fun toCData() = ConfigurationData(
        preProcessingStrategies.mapValues { getAnnotationName(it.value) },
        variableSelectionStrategies.mapValues { getAnnotationName(it.value) },
        domainSelectionStrategies.mapValues { getAnnotationName(it.value) },
        constraintPropagationStrategies.mapValues { getAnnotationName(it.value) },
        speedRange
    )

    fun toCSPConfiguration(c: Configuration): CSPConfiguration {
        val preProcessingStrategy = preProcessingStrategies.getOrFirst(
            c.preProcessingStrategy?.toInt()
        ).getDeclaredConstructor().newInstance()

        val variableSelectionStrategy = variableSelectionStrategies.getOrFirst(
            c.variableSelectionHeuristic?.toInt()
        ).getDeclaredConstructor().newInstance()

        val domainSelectionStrategy = domainSelectionStrategies.getOrFirst(
            c.domainSelectionHeuristic?.toInt()
        ).getDeclaredConstructor().newInstance()

        val constraintPropagationStrategy = constraintPropagationStrategies.getOrFirst(
            c.constraintPropagationStrategy?.toInt()
        ).getDeclaredConstructor().newInstance()

        val speed: Int = c.backtrackingSpeed ?: ((speedRange.first + speedRange.second) / 2)

        return CSPConfiguration(
            preProcessingStrategy,
            variableSelectionStrategy,
            domainSelectionStrategy,
            constraintPropagationStrategy,
            speed
        )

    }

    private fun getAnnotationName(c: Class<*>): String {
        val hasName = c.annotations.find { it is HasName } as? HasName
        return hasName?.name ?: ""
    }

}

fun <K : Int?, V> Map<out K, V>.getOrFirst(index: Int?): V = getOrDefault(index, values.first())