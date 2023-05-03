package com.sidgowda.pawcalc.data.dogs

inline fun <T> MutableList<T>.mapInPlace(mutator: (T)->T) {
    val iterate = this.listIterator()
    while (iterate.hasNext()) {
        val oldValue = iterate.next()
        val newValue = mutator(oldValue)
        if (newValue !== oldValue) {
            iterate.set(newValue)
        }
    }
}

inline fun <T> List<T>.update(action: (MutableList<T>) -> Unit): List<T> {
    return toMutableList().apply {
        action(this)
    }
}
