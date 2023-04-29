package com.sidgowda.pawcalc.data.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    @Singleton
    @Provides
    @Named("ioScope")
    fun providesCoroutineScopeWithIODispatcher(
        @Named("io") coroutineDispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + coroutineDispatcher)
    }

    @Singleton
    @Provides
    @Named("computationScope")
    fun providesCoroutineScopeWithComputationDispatcher(
        @Named("computation") coroutineDispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + coroutineDispatcher)
    }
}
