package com.sidgowda.pawcalc.data.dogs.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

fun <T> Flow<T>.throttleFirst(timeoutMillis: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        Timber.tag("FlowUtils").d("Current Time = $currentTime, lastEmissionTime = $lastEmissionTime")
        val mayEmit = currentTime - lastEmissionTime > timeoutMillis
        if (mayEmit) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}
