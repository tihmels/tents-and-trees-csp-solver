package de.tihmels

import kotlinx.serialization.Serializable

@Serializable
data class Location(val row: Int, val column: Int)

@Serializable
data class TentsAndTrees(
    val id: Int? = null,
    val rows: Int? = null,
    val cols: Int? = null,
    val tentsPerRow: List<Int> = emptyList(),
    val tentsPerCol: List<Int> = emptyList(),
    val trees: List<Location> = emptyList(),
    val variables: List<Location> = emptyList(),
)

@Serializable
enum class Tile {
    EMPTY, GRASS, TENT, TREE;
}

@Serializable
enum class Domain {
    GRASS, TENT_TO_TOP, TENT_TO_RIGHT, TENT_TO_BOTTOM, TENT_TO_LEFT;

    val isTent: Boolean
        get() = this == TENT_TO_TOP || this == TENT_TO_RIGHT || this == TENT_TO_BOTTOM || this == TENT_TO_LEFT

    fun toTile(): Tile = if (isTent) Tile.TENT else Tile.GRASS

}

@Serializable
data class LocationAndValue(val location: Location, val value: Domain)

@Serializable
data class Assignment(val assignments: List<LocationAndValue> = emptyList())

@Serializable
data class CSPStatistics(
    val totalSteps: Int = 0,
    val totalErrors: Int = 0,
    val deadEnds: Int = 0,
    val solved: Boolean = false
)

@Serializable
data class ConfigurationData(
    val preProcessingStrategies: Map<Int, String> = mutableMapOf(),
    val variableSelectionStrategies: Map<Int, String> = mutableMapOf(),
    val domainSelectionStrategies: Map<Int, String> = mutableMapOf(),
    val constraintPropagationStrategies: Map<Int, String> = mutableMapOf(),
    val speedRange: Pair<Int, Int>? = null
)

@Serializable
data class Configuration(
    val preProcessingStrategy: String? = null,
    val variableSelectionHeuristic: String? = null,
    val domainSelectionHeuristic: String? = null,
    val constraintPropagationStrategy: String? = null,
    val backtrackingSpeed: Int? = null
)

@Serializable
enum class BacktrackingState {
    STOPPED, RUNNING, PAUSED
}

@Serializable
data class CMessage(val messageType: CMessageType)

@Serializable
data class SMessage(val messageType: SMessageType)

@Serializable
sealed class CMessageType {

    @Serializable
    object GetPuzzle : CMessageType()

    @Serializable
    object FetchConfigurationData : CMessageType()

    @Serializable
    data class SetBacktrackingState(val backtracking: BacktrackingState) : CMessageType()

    @Serializable
    data class SetConfiguration(val configuration: Configuration) : CMessageType()

}

@Serializable
sealed class SMessageType {

    @Serializable
    data class PuzzleUpdate(val puzzle: TentsAndTrees) : SMessageType()

    @Serializable
    data class ConfigurationDataUpdate(val configurationData: ConfigurationData) : SMessageType()

    @Serializable
    data class AssignmentStateUpdate(val assignment: Assignment, val statistics: CSPStatistics) : SMessageType()

    @Serializable
    data class BacktrackingStateUpdate(val state: BacktrackingState) : SMessageType()

}