package com.sidgowda.pawcalc.data.dogs.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.throttleFirst(timeoutMillis: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        val mayEmit = currentTime - lastEmissionTime > timeoutMillis
        if (mayEmit) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}
