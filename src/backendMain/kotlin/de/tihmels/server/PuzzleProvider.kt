package de.tihmels.server

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import de.tihmels.Tile
import de.tihmels.Location
import de.tihmels.TentsAndTrees
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

object PuzzleProvider {

    private val atomicId = AtomicInteger(0)
    private val puzzles: List<TentsAndTrees>

    private val resourcesRoot = System.getenv("RROOT") ?: ""
    private const val puzzleFolder = "testPuzzles/"

    fun getRandomPuzzle() = puzzles.random()

    private fun getPuzzleFromPath(path: Path): TentsAndTrees {

        val rows = csvReader().readAll(path.toFile())

        val tentsPerColumn = rows.first().stream().skip(1).map { it.toInt() }.collect(Collectors.toList())

        val tentsPerRow: List<Int> = rows.stream().skip(1)
            .map { it[0].toInt() }
            .collect(Collectors.toList())

        val tileList = rows.stream().skip(1)
            .map { it.stream().skip(1).collect(Collectors.toList()) }
            .map(::getTileList)
            .collect(Collectors.toList())

        val trees = mutableListOf<Location>()
        val variables = mutableListOf<Location>()

        for (row in tentsPerRow.indices) {
            for (col in tentsPerColumn.indices) {
                val l = Location(row, col)
                if (tileList[row][col] == Tile.EMPTY) variables.add(l) else trees.add(l)
            }
        }

        return TentsAndTrees(
            atomicId.incrementAndGet(),
            tentsPerRow.size,
            tentsPerColumn.size,
            tentsPerRow,
            tentsPerColumn,
            trees,
            variables
        )
    }

    private fun getTileList(line: List<String>): List<Tile> =
        line.map { if (it == "t") Tile.TREE else Tile.EMPTY }

    init {

        val projectDirAbsolutePath = Paths.get(resourcesRoot).toAbsolutePath()
        val resourcesPath = Paths.get(projectDirAbsolutePath.toString(), "processedResources/backend/main/$puzzleFolder")

        puzzles = Files.walk(resourcesPath)
            .filter(Files::isRegularFile)
            .filter(Path::isCsv)
            .map(::getPuzzleFromPath)
            .collect(Collectors.toList())
    }

}

fun Path.isCsv() = toString().endsWith(".csv")