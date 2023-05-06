package com.sidgowda.pawcalc.data.dogs

inline fun <T> MutableList<T>.mapInPlace(mutator: (T) -> T) {
    val iterator = this.listIterator()
    while (iterator.hasNext()) {
        val oldValue = iterator.next()
        val newValue = mutator(oldValue)
        if (newValue !== oldValue) {
            iterator.set(newValue)
        }
    }
}

inline fun <T> List<T>.update(action: (MutableList<T>) -> Unit): List<T> {
    return toMutableList().apply {
        action(this)
    }
}
