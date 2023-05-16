package com.sidgowda.pawcalc

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.NoActivityResumedException
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sidgowda.pawcalc.data.modules.OnboardingDataModule
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepoImpl
import com.sidgowda.pawcalc.db.di.DbModule
import com.sidgowda.pawcalc.navigation.*
import com.sidgowda.pawcalc.test.IdlingResourceCoroutineDispatcher
import com.sidgowda.pawcalc.test.TestTags
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@RunWith(AndroidJUnit4::class)
@UninstallModules(DbModule::class, OnboardingDataModule::class)
@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<PawCalcActivity>()

    @Inject
    @Named("io")
    lateinit var ioIdlingDispatcher: IdlingResourceCoroutineDispatcher

    @Inject
    @Named("computation")
    lateinit var computationIdlingDispatcher: IdlingResourceCoroutineDispatcher

    @BindValue
    @JvmField
    val onboardingDataSource = FakeOnboardingDataSourceSingleton

    @BindValue
    @JvmField
    val onboardingRepo: OnboardingRepo = OnboardingRepoImpl(onboardingDataSource)

    @Before
    fun setup() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(ioIdlingDispatcher)
        IdlingRegistry.getInstance().register(computationIdlingDispatcher)
    }

    @After
    fun cleanup() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.NotOnboarded)
        IdlingRegistry.getInstance().unregister(ioIdlingDispatcher)
        IdlingRegistry.getInstance().unregister(computationIdlingDispatcher)
    }

    @Test
    fun Onboarding_Displayed_By_Default() {
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test(expected = NoActivityResumedException::class)
    fun Clicking_Back_Button_On_Onboarding_Closes_App() {
        Espresso.pressBack()
    }

    @Test
    fun Clicking_On_Add_Dog_Button_Navigates_To_New_Dog_From_Onboarding() {
        composeTestRule.onNodeWithTag(
            TestTags.Onboarding.TAG_ADD_DOG_BUTTON
        ).performClick()

        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_Close_Icon_On_New_Dog_NavigatesBack_To_Dog_List() {
        composeTestRule.apply {
            onNodeWithTag(TestTags.Onboarding.TAG_ADD_DOG_BUTTON).performClick()
            onNodeWithContentDescription(
                InstrumentationRegistry.getInstrumentation().targetContext.getString(
                    R.string.cd_close_nav_icon
                )
            ).performClick()

            onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertIsDisplayed()
        }
    }

    @Test
    fun Clicking_Back_Button_On_New_Dog_Navigates_To_Dog_List() {
        composeTestRule.onNodeWithTag(TestTags.Onboarding.TAG_ADD_DOG_BUTTON).performClick()
        Espresso.pressBack()

        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Settings_Icon_In_Dog_List_Navigates_To_Settings() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Top_Bar_Back_Arrow_Navigates_Back_To_Dog_List() {
        composeTestRule.onNodeWithTag(TestTags.Onboarding.TAG_ADD_DOG_BUTTON).performClick()
        Espresso.pressBack()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertIsDisplayed()
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

        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertIsDisplayed()
    }


    @Test(expected = NoActivityResumedException::class)
    fun Clicking_Back_Button_On_Dog_List_Closes_App() {
        composeTestRule.onNodeWithTag(TestTags.Onboarding.TAG_ADD_DOG_BUTTON).performClick()
        Espresso.pressBack()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertIsDisplayed()
        Espresso.pressBack()
    }

    @Test
    fun DogList_Displayed_As_Default_After_User_Has_Onboarded() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Add_Dog_Button_From_Dog_Details_Navigates_To_New_Dog() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(
            TestTags.DogList.TAG_ADD_DOG_BUTTON
        ).performClick()

        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Any_Dog_In_DogList_Navigates_To_Dog_Details() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()

        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Edit_Button_In_Dog_Details_Navigates_To_Edit_Dog() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()
        composeTestRule.onNodeWithTag(TestTags.DogDetails.TAG_EDIT_BUTTON).performClick()

        composeTestRule.onNodeWithTag(EDIT_DOG_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Settings_Icon_Navigates_To_Settings_Screen_From_Onboarding() {
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun NavigatingBack_From_Settings_Navigates_To_Onboarding() {
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_press_back
            )
        ).performClick()

        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Settings_Icons_Navigate_To_Settings_From_Dog_List() {

    }

    @Test
    fun Clicking_On_Settings_Icons_Navigate_To_Settings_From_Dog_Details() {

    }

    @Test
    fun Clicking_On_Settings_Icons_Navigate_To_Settings_From_Edit_Dog() {

    }

    @Test
    fun Clicking_On_Settings_Icons_Navigate_To_Settings_From_New_Dog() {

    }

}
