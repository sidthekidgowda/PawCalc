package com.sidgowda.pawcalc.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.NoActivityResumedException
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sidgowda.pawcalc.R
import com.sidgowda.pawcalc.data.modules.OnboardingDataModule
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepoImpl
import com.sidgowda.pawcalc.db.di.DbModule
import com.sidgowda.pawcalc.navigation.*
import com.sidgowda.pawcalc.test.IdlingResourceCoroutineDispatcher
import com.sidgowda.pawcalc.test.TestTags
import com.sidgowda.pawcalc.test.fakes.FakeOnboardingDataSourceSingleton
import com.sidgowda.pawcalc.ui.PawCalcActivity
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.*
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalTestApi::class)
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
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertExists()
    }

    @Test(expected = NoActivityResumedException::class)
    fun Clicking_Back_Button_On_Onboarding_Closes_App() {
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertExists()
        Espresso.pressBack()
    }

    @Ignore
    @Test
    fun Clicking_On_Add_Dog_Button_Navigates_To_New_Dog_From_Onboarding() {
        // failed
        // Onboarding
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(
            TestTags.Onboarding.TAG_ADD_DOG_BUTTON
        ).performClick()

        // New Dog screen
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_add_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertExists()
    }

    @Ignore
    @Test
    fun Navigating_To_New_Dog_From_Onboarding_And_Clicking_Close_Icon_Navigates_Back_To_Dog_List() {
        // failed
        composeTestRule.apply {
            // Onboarding
            composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertExists()
            onNodeWithTag(TestTags.Onboarding.TAG_ADD_DOG_BUTTON).performClick()

            // New Dog
            composeTestRule.onNodeWithText(
                InstrumentationRegistry.getInstrumentation().targetContext.getString(
                    R.string.title_add_dog
                )
            ).assertIsDisplayed()
            onNodeWithContentDescription(
                InstrumentationRegistry.getInstrumentation().targetContext.getString(
                    R.string.cd_close_nav_icon
                )
            ).performClick()

            // Dog List
            composeTestRule.onNodeWithText(
                InstrumentationRegistry.getInstrumentation().targetContext.getString(
                    R.string.title_home
                )
            ).assertIsDisplayed()
            onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        }
    }

    @Test
    fun Clicking_Back_Button_On_New_Dog_Navigates_To_Dog_List() {
        // failed
        // Onboarding
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.Onboarding.TAG_ADD_DOG_BUTTON).performClick()
        Espresso.pressBackUnconditionally()

        // wait till activity is resumed
        composeTestRule.waitForIdle()

        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_On_Settings_Icon_In_Dog_List_Navigates_To_Settings() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
    }

    @Ignore
    @Test
    fun Clicking_On_Top_Bar_Back_Arrow_Navigates_Back_To_Dog_List() {
        // failed
        // Onboarding
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(
            TestTags.Onboarding.TAG_ADD_DOG_BUTTON
        ).performClick()

        // New Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_add_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_close_nav_icon
            )
        ).performClick()

        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
    }


    @Test(expected = NoActivityResumedException::class)
    fun Clicking_Back_Button_On_Dog_List_Closes_App() {
        // Onboarding
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(
            TestTags.Onboarding.TAG_ADD_DOG_BUTTON
        ).performClick()
        Espresso.pressBack()

        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        Espresso.pressBack()
    }

    @Test
    fun DogList_Displayed_As_Default_After_User_Has_Onboarded() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_On_Add_Dog_Button_From_Dog_Details_Navigates_To_New_Dog() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(
            TestTags.DogList.TAG_ADD_DOG_BUTTON
        ).performClick()

        // New Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_add_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_Close_Navigates_Back_To_Dog_List_From_New_Dog() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(
            TestTags.DogList.TAG_ADD_DOG_BUTTON
        ).performClick()

        // New Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_add_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_close_nav_icon
            )
        ).performClick()

        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_On_Any_Dog_In_DogList_Navigates_To_Dog_Details() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_Back_In_Dog_Details_Navigates_Back_To_Dog_List() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_press_back
            )
        ).performClick()

        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_On_Edit_Button_In_Dog_Details_Navigates_To_Edit_Dog() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogDetails.TAG_EDIT_BUTTON).performClick()

        // Edit Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_edit_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(EDIT_DOG_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_Close_On_Edit_Dog_Navigates_Back_To_Dog_Details() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogDetails.TAG_EDIT_BUTTON).performClick()

        // Edit Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_edit_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(EDIT_DOG_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_close_nav_icon
            )
        ).performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogDetails.TAG_EDIT_BUTTON).performClick()
    }

    @Test
    fun Clicking_On_Settings_Icon_Navigates_To_Settings_Screen_From_Onboarding() {
        // Onboarding
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Navigating_Back_From_Settings_Navigates_To_Onboarding() {
        // Onboarding
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_press_back
            )
        ).performClick()

        // Onboarding
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ONBOARDING_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_On_Settings_Icon_Navigates_To_Settings_From_Dog_List() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_On_Back_Arrow_Navigates_Back_To_Dog_List_From_Settings() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_press_back
            )
        ).performClick()

        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
    }

    @Ignore("Need to investigate flaky test")
    @Test
    fun Clicking_On_Settings_Icon_Navigates_To_Settings_From_Dog_Details() {
        // failed
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_press_back
            )
        ).performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_On_Back_Arrow_Navigates_Back_To_Dog_Details_From_Settings() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_press_back
            )
        ).performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()
    }

    @Test
    fun Clicking_On_Settings_Icon_Navigates_To_Settings_From_Edit_Dog() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogDetails.TAG_EDIT_BUTTON).performClick()

        // Edit Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_edit_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(EDIT_DOG_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_On_Back_Arrow_Navigates_Back_To_Edit_Dog_From_Settings() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()

        // Dog Details
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_DETAILS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogDetails.TAG_EDIT_BUTTON).performClick()

        // Edit Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_edit_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(EDIT_DOG_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_press_back
            )
        ).performClick()

        // Edit Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_edit_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(EDIT_DOG_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_On_Settings_Icon_Navigates_To_Settings_From_New_Dog() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_ADD_DOG_BUTTON).performClick()

        // New Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_add_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
    }

    @Test
    fun Clicking_On_Back_Arrow_Navigates_Back_To_New_Dog_From_Settings() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        // Dog List
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_ADD_DOG_BUTTON).performClick()

        // New Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_add_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()

        // Settings
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SETTINGS_SCREEN_ROUTE).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_press_back
            )
        ).performClick()

        // New Dog
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_add_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertExists()
    }
}
