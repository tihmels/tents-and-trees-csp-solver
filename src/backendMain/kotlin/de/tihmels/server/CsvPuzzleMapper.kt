package de.tihmels.server

import de.tihmels.Location
import de.tihmels.TentsAndTrees
import de.tihmels.Tile
import de.tihmels.misc.Mapper
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

object CsvPuzzleMapper : Mapper<List<List<String>>, TentsAndTrees> {

    private val ids = AtomicInteger()

    override fun map(value: List<List<String>>): TentsAndTrees {

        val tentsPerColumn = value.first().stream().skip(1).map { it.toInt() }.collect(Collectors.toList())

        val tentsPerRow: List<Int> = value.stream().skip(1)
            .map { it[0].toInt() }
            .collect(Collectors.toList())

        val tileList = value.stream().skip(1)
            .map { it.stream().skip(1).collect(Collectors.toList()) }
            .map(this::getTileList)
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
            ids.getAndIncrement(),
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

}