package de.tihmels.misc

import java.util.function.Supplier

class RandomSupplier<T>(private val list: List<T>) : Supplier<T> {

    private val randomIndices = (list.indices).shuffled()
    private var index = randomIndices.first()

    override fun get(): T = list[++index % randomIndices.size]

}