package com.sidgowda.pawcalc.data

import com.sidgowda.pawcalc.data.dispatchers.DispatchersModule
import com.sidgowda.pawcalc.test.IdlingResourceCoroutineDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

object TestModule {

    @Module
    @TestInstallIn(
        components = [SingletonComponent::class],
        replaces = [DispatchersModule::class]
    )
    object TestDispatchers {

        @Singleton
        @Provides
        @Named("io")
        fun providesIoDispatcher(): CoroutineDispatcher {
            return IdlingResourceCoroutineDispatcher(Dispatchers.IO)
        }

        @Singleton
        @Provides
        @Named("computation")
        fun providesComputationDispatcher(): CoroutineDispatcher {
            return IdlingResourceCoroutineDispatcher(Dispatchers.Default)
        }

        // convenience functions to avoid casting in tests
        @Provides
        @Named("io_idling")
        fun providesTestIOIdlingDispatcher(
            @Named("io") dispatcher: CoroutineDispatcher
        ): IdlingResourceCoroutineDispatcher {
            return dispatcher as IdlingResourceCoroutineDispatcher
        }

        @Provides
        @Named("computation_idling")
        fun providesTestComputationIdlingDispatcher(
            @Named("computation") dispatcher: CoroutineDispatcher
        ): IdlingResourceCoroutineDispatcher {
            return dispatcher as IdlingResourceCoroutineDispatcher
        }
    }

}
