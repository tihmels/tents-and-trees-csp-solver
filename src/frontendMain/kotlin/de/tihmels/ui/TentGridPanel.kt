package de.tihmels.ui

import de.tihmels.Location
import de.tihmels.ClientStateService
import de.tihmels.TentsAndTrees
import de.tihmels.Tile
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.GridAutoFlow
import io.kvision.core.JustifyItems
import io.kvision.html.Div
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.panel.GridPanel
import io.kvision.panel.gridPanel
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.utils.perc

const val TENT_IMG = "img/tent.png"
const val GRASS_IMG = "img/grass.jpg"
const val TREE_IMG = "img/tree.png"

const val GRID_CELL_SIZE = "minmax(25px, 50px)"
const val GRID_GAP = 3

fun Container.tentGrid(puzzle: TentsAndTrees?) {

    puzzle?.let {
        gridPanel(columnGap = GRID_GAP, rowGap = GRID_GAP) {
            id = "tent-grid"

            width = 100.perc
            gridAutoRows = "1fr"
            gridTemplateColumns = "repeat(${it.cols?.plus(1)}, $GRID_CELL_SIZE)"

            tentsPerColumn(it.tentsPerCol)
            tentsPerRow(it.tentsPerRow)
            innerGrid(it)
        }
    }
}

private fun GridPanel.tentsPerRow(tentsPerRow: List<Int>) {

    val rows = tentsPerRow.size

    options(columnStart = 1, rowStart = 2, rowEnd = (rows.plus(2)).toString()) {
        gridPanel(justifyItems = JustifyItems.CENTER, alignItems = AlignItems.CENTER, rowGap = GRID_GAP) {

            gridAutoRows = "1fr"
            height = 100.perc

            for (r in 0 until rows) {
                div(tentsPerRow[r].toString())
            }
        }
    }
}

private fun GridPanel.tentsPerColumn(tentsPerCol: List<Int>) {

    val cols = tentsPerCol.size

    options(rowStart = 1, columnStart = 2, columnEnd = (cols.plus(2)).toString()) {
        gridPanel(justifyItems = JustifyItems.CENTER, alignItems = AlignItems.CENTER, columnGap = GRID_GAP) {

            gridAutoFlow = GridAutoFlow.COLUMN
            height = 100.perc

            for (c in 0 until cols) {
                div(tentsPerCol[c].toString())
            }
        }
    }
}

private fun GridPanel.innerGrid(puzzle: TentsAndTrees) {
    options(
        rowStart = 2,
        columnStart = 2,
        rowEnd = (puzzle.rows?.plus(2)).toString(),
        columnEnd = (puzzle.cols?.plus(2)).toString()
    ) {
        gridPanel(
            justifyItems = JustifyItems.STRETCH,
            alignItems = AlignItems.CENTER,
            rowGap = GRID_GAP,
            columnGap = GRID_GAP
        ) {

            gridAutoRows = "1fr"
            gridAutoColumns = "1fr"

            val assignment = ClientStateService.puzzleState.sub { it.assignment }

            bind(assignment) { a ->

                for (location in puzzle.trees) {
                    setLocation(location, Tile.TREE)
                }

                for (location in puzzle.variables) {
                    val tile = a?.assignments?.find { it.location == location }?.value?.toTile()
                        ?: Tile.EMPTY

                    setLocation(
                        location,
                        tile
                    )
                }

            }
        }
    }
}

private fun GridPanel.setLocation(location: Location, domain: Tile) {
    options(
        columnStart = location.column + 1,
        rowStart = location.row + 1,
        alignSelf = AlignItems.STRETCH,
        justifySelf = JustifyItems.STRETCH
    ) {
        getTile(domain)
    }
}


fun Container.getTile(domain: Tile): Div = when (domain) {
    Tile.EMPTY -> {
        div(className = "border border-secondary") {
            height = 100.perc
        }
    }
    Tile.GRASS -> {
        div {
            image(GRASS_IMG, responsive = true)
        }
    }
    Tile.TENT -> {
        div {
            image(TENT_IMG, responsive = true)
        }
    }
    Tile.TREE -> {
        div {
            image(TREE_IMG, responsive = true)
        }
    }
}