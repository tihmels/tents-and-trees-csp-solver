package de.tihmels.csp

import de.tihmels.Location
import java.util.stream.Collectors
import kotlin.math.abs

fun Location.getSurroundingLocations(locations: List<Location>, diagonal: Boolean): List<Location> =
    locations.stream()
        .filter { l: Location -> l != this }
        .filter { l: Location ->
            if (diagonal) rowDistance(l) <= 1 && colDistance(l) <= 1 else
                rowDistance(l) == 1 && colDistance(l) == 0 || rowDistance(l) == 0 && colDistance(l) == 1
        }
        .collect(Collectors.toList())

fun Location.getTentQuadrant(locations: List<Location>): MutableList<Location> = locations.stream()
    .filter { l: Location -> l == this || l.row == row + 1 && l.column == column || l.row == row && l.column == column + 1 || l.row == row + 1 && l.column == column + 1 }
    .collect(Collectors.toList())

val Location.locationAbove: Location
    get() = Location(row - 1, column)
val Location.locationRight: Location
    get() = Location(row, column + 1)
val Location.locationBelow: Location
    get() = Location(row + 1, column)
val Location.locationLeft: Location
    get() = Location(row, column - 1)

private fun Location.rowDistance(l: Location): Int = abs(l.row - row)

private fun Location.colDistance(l: Location): Int = abs(l.column - column)

fun Location.isRightTo(l: Location): Boolean = locationLeft == l

fun Location.isLeftTo(l: Location): Boolean = locationRight == l

fun Location.isBelow(l: Location): Boolean = locationAbove == l

fun Location.isAbove(l: Location): Boolean = locationBelow == l