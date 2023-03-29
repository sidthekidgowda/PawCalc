package com.sidgowda.pawcalc

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.NoActivityResumedException
import androidx.test.platform.app.InstrumentationRegistry
import com.sidgowda.pawcalc.newdog.navigation.NEW_DOG_SCREEN_ROUTE
import com.sidgowda.pawcalc.onboarding.OnboardingSingleton
import com.sidgowda.pawcalc.onboarding.TestTags.TAG_ADD_DOG_BUTTON
import com.sidgowda.pawcalc.onboarding.navigation.ONBOARDING_ROUTE
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<PawCalcActivity>()

    @After
    fun cleanup() {
        OnboardingSingleton.reset()
    }

    @Test
    fun Onboarding_Displayed_By_Default() {
        composeTestRule.onNodeWithTag(ONBOARDING_ROUTE).assertIsDisplayed()
    }

    @Test(expected = NoActivityResumedException::class)
    fun Clicking_Back_Button_On_Onboarding_Closes_App() {
        pressBack()
    }

    @Test
    fun Clicking_On_Add_Dog_Button_Navigates_To_New_Dog() {
        composeTestRule.onNodeWithTag(
            TAG_ADD_DOG_BUTTON
        ).performClick()

        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_Close_Icon_On_New_Dog_Navigates_To_Dog_List() {
        composeTestRule.apply {
            onNodeWithTag(TAG_ADD_DOG_BUTTON).performClick()
            onNodeWithContentDescription(
                InstrumentationRegistry.getInstrumentation().targetContext.getString(
                    R.string.cd_close_nav_icon
                )
            ).performClick()

            onNodeWithTag(DOG_LIST_ROUTE).assertIsDisplayed()
        }
    }

    @Test
    fun Clicking_Back_Button_On_New_Dog_Navigates_To_Dog_List() {
        composeTestRule.onNodeWithTag(TAG_ADD_DOG_BUTTON).performClick()
        pressBack()
        composeTestRule.onNodeWithTag(DOG_LIST_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Settings_Icon_In_Dog_List_Navigates_To_Settings() {
        composeTestRule.onNodeWithTag(TAG_ADD_DOG_BUTTON).performClick()
        pressBack()
        composeTestRule.onNodeWithTag(DOG_LIST_ROUTE).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()
        composeTestRule.onNodeWithTag(SETTINGS_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Top_Bar_Back_Arrow_Navigates_Back_To_Dog_List() {
        composeTestRule.onNodeWithTag(TAG_ADD_DOG_BUTTON).performClick()
        pressBack()
        composeTestRule.onNodeWithTag(DOG_LIST_ROUTE).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()
       composeTestRule.onNodeWithContentDescription(
           InstrumentationRegistry.getInstrumentation().targetContext.getString(
               R.string.cd_press_back
           )
       ).performClick()

        composeTestRule.onNodeWithTag(DOG_LIST_ROUTE).assertIsDisplayed()
    }


    @Test(expected = NoActivityResumedException::class)
    fun Clicking_Back_Button_On_Dog_List_Closes_App() {
        composeTestRule.onNodeWithTag(TAG_ADD_DOG_BUTTON).performClick()
        pressBack()
        composeTestRule.onNodeWithTag(DOG_LIST_ROUTE).assertIsDisplayed()
        pressBack()
    }
}
