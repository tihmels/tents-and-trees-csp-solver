package de.tihmels.server

import de.tihmels.Configuration
import de.tihmels.ConfigurationData
import de.tihmels.annotation.HasName
import de.tihmels.csp.CSPConfiguration
import de.tihmels.csp.heuristic.value.ISelectDomainHeuristic
import de.tihmels.csp.heuristic.value.LeastConstrainingValueOrderHeuristic
import de.tihmels.csp.heuristic.value.LexicographicDomainOrderHeuristic
import de.tihmels.csp.heuristic.value.RandomValueOrderHeuristic
import de.tihmels.csp.heuristic.variable.*
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
        listOf(
            NoPreProcessing::class.java,
            TentsAndTreesPreProcessor::class.java
        ).mapByIndex()

    private val variableSelectionStrategies: Map<Int, Class<out ISelectVariableHeuristic>> =
        listOf(
            SelectFirstUnassignedVariableHeuristic::class.java,
            RandomVariableHeuristic::class.java,
            MinimumRemainingValuesHeuristic::class.java,
            MostConstrainingVariableHeuristic::class.java,
            MostConstrainingVariableTieBreakerHeuristic::class.java
        ).mapByIndex()

    private val domainSelectionStrategies: Map<Int, Class<out ISelectDomainHeuristic>> =
        listOf(
            LexicographicDomainOrderHeuristic::class.java,
            RandomValueOrderHeuristic::class.java,
            LeastConstrainingValueOrderHeuristic::class.java
        ).mapByIndex()

    private val constraintPropagationStrategies: Map<Int, Class<out IConstraintPropagation>> =
        listOf(
            NoConstraintPropagation::class.java,
            ForwardChecking::class.java,
            TentsAndTreesForwardChecking::class.java
        ).mapByIndex()

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

private val variableSelectionStrategies: Map<Int, Class<out ISelectVariableHeuristic>> =
    listOf(
        SelectFirstUnassignedVariableHeuristic::class.java,
        RandomVariableHeuristic::class.java,
        MinimumRemainingValuesHeuristic::class.java,
        MostConstrainingVariableHeuristic::class.java,
        MostConstrainingVariableTieBreakerHeuristic::class.java
    ).mapIndexed { index, clazz -> index to clazz }.toMap()


fun <K> List<K>.mapByIndex(): Map<Int, K> = mapIndexed { index, k -> index to k }.toMap()

fun <K : Int?, V> Map<out K, V>.getOrFirst(index: Int?): V = getOrDefault(index, values.first())