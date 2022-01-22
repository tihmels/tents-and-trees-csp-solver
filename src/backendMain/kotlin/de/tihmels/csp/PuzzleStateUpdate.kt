package de.tihmels.csp

import de.tihmels.CSPStatistics
import de.tihmels.Domain
import de.tihmels.Location

data class PuzzleStateUpdate(val assignment: Map<Location, Domain>, val statistics: CSPStatistics)