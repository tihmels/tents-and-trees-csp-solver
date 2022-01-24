package de.tihmels.server

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.common.collect.ImmutableList
import de.tihmels.Constants
import de.tihmels.TentsAndTrees
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object PuzzleLoader {

    val puzzles: List<TentsAndTrees>

    private val resourcesRoot = Constants.Resource.RESOURCES_ROOT
    private const val puzzleFolder = Constants.Resource.PUZZLE_FOLDER

    init {

        val resourcesPathAbsolute = Paths.get(resourcesRoot).toAbsolutePath()
        val resourcesPath =
            Paths.get(resourcesPathAbsolute.toString(), "processedResources/backend/main/$puzzleFolder")

        puzzles = Files.walk(resourcesPath)
            .filter(Files::isRegularFile)
            .filter(Path::isCsv)
            .map { csvReader().readAll(it.toFile()) }
            .map(CsvPuzzleMapper::map)
            .collect(ImmutableList.toImmutableList())
    }

}

fun Path.isCsv() = toString().endsWith(".csv")