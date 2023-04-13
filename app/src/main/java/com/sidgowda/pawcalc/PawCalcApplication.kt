package com.sidgowda.pawcalc

import android.app.Application
import android.util.Log
import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSource
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PawCalcApplication : Application() {

    @Inject
    lateinit var onboardingDataSource: OnboardingDataSource

    private val scope = MainScope()
    override fun onCreate() {
        super.onCreate()
        intialize()
    }

    private fun intialize() {
        initializeOnboarding()
        initalizeSettings()
    }

    private fun initializeOnboarding() {
        scope.launch(Dispatchers.Default) {
            onboardingDataSource.onboardingState.collect {
                Log.d("PawCalcApp", "Initialized Onboarding Datasource")
            }
        }
    }

    private fun initalizeSettings() {

    }

}
