package com.sidgowda.pawcalc.test

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

class IdlingResourceCoroutineDispatcher(
    private val dispatcher: CoroutineDispatcher
) : CoroutineDispatcher(), IdlingResource  {

    private val counter: CountingIdlingResource =
        CountingIdlingResource("EspressoIdlingResourceCoroutineDispatcher for $dispatcher")

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        counter.increment()
        val blockWithDecrement = Runnable {
            try {
                block.run()
            } finally {
                counter.decrement()
            }
        }
        dispatcher.dispatch(context, blockWithDecrement)
    }

    override fun getName(): String {
       return counter.name
    }

    override fun isIdleNow(): Boolean {
        return counter.isIdleNow
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        counter.registerIdleTransitionCallback(callback)
    }
}
