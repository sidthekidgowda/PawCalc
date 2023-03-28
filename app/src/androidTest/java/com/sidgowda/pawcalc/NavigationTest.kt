package com.sidgowda.pawcalc

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<PawCalcActivity>()

    lateinit var navController: TestNavHostController

    @Test
    fun Onboarding_Displayed_By_Default() {
        hiltRule.inject()
        composeTestRule.apply {
            onNodeWithText("Welcome to PawCalc. Find out how old your dog is in accurate human years.").assertIsDisplayed()
        }
    }

}
