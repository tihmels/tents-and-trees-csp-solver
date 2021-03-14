package de.tihmels.csp.constraint

import de.tihmels.Domain
import de.tihmels.Location
import java.util.*

// V is the variable type and D is the domain type
abstract class Constraint(val variables: List<Location>) {

    val id = UUID.randomUUID().toString()

    abstract fun satisfied(assignment: Map<Location, Domain>): Boolean

}