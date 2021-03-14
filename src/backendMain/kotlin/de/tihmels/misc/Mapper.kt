package de.tihmels.misc

interface Mapper<in T, out U> {

    fun map(value: T): U

}