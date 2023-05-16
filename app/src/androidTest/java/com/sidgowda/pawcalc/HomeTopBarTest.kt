package com.sidgowda.pawcalc

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sidgowda.pawcalc.data.modules.OnboardingDataModule
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepoImpl
import com.sidgowda.pawcalc.db.di.DbModule
import com.sidgowda.pawcalc.test.IdlingResourceCoroutineDispatcher
import com.sidgowda.pawcalc.test.TestTags
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.*
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
class HomeTopBarTest {

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
    fun When_On_Onboarding_TopBar_Title_Is_PawCalc() {
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_Settings_TopBar_Title_Is_Settings() {
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_DogList_TopBar_Title_Is_PawCalc() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_home
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_DogDetails_TopBar_Title_Is_Details() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_EditDog_TopBar_Title_Is_Edit() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()
        composeTestRule.onNodeWithTag(TestTags.DogDetails.TAG_EDIT_BUTTON).performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_edit_dog
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_NewDog_TopBar_Title_Is_Add() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_ADD_DOG_BUTTON).performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_add_dog
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_Onboarding_No_Nav_Icon() {
        composeTestRule.onNodeWithTag(TestTags.App.TAG_NAV_ICON_BUTTON).assertDoesNotExist()
    }

    @Test
    fun When_On_Onboarding_ActionIcon_Is_Settings() {
        composeTestRule.onNodeWithTag(TestTags.App.TAG_ACTION_ICON_BUTTON).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_Dog_List_No_Nav_Icon() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.App.TAG_NAV_ICON_BUTTON).assertDoesNotExist()
    }

    @Test
    fun When_On_Dog_List_Action_Icon_Is_Settings() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.App.TAG_ACTION_ICON_BUTTON).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_Settings_TopBar_Nav_Icon_Is_Back_Button() {
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.App.TAG_NAV_ICON_BUTTON).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_press_back
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_Settings_TopBar_Action_Icon_Does_Not_Exist() {
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_settings
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.App.TAG_ACTION_ICON_BUTTON).assertDoesNotExist()
    }

    @Test
    fun When_On_DogDetails_TopBar_Nav_Icon_Is_Back_Button() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(0)
            .performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.App.TAG_NAV_ICON_BUTTON).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_press_back
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_DogDetails_TopBar_Action_Icon_Is_Settings() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_dog_details
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.App.TAG_ACTION_ICON_BUTTON).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_EditDog_TopBar_Nav_Icon_Is_Close() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag(TestTags.DogDetails.TAG_EDIT_BUTTON).performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_edit_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.App.TAG_NAV_ICON_BUTTON).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_close_nav_icon
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_EditDog_TopBar_Action_Icon_Is_Settings() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_DOG_LIST_CONTENT)
            .onChildAt(1)
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag(TestTags.DogDetails.TAG_EDIT_BUTTON).performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_edit_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.App.TAG_ACTION_ICON_BUTTON).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_NewDog_TopBar_Nav_Icon_Is_Close() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_ADD_DOG_BUTTON).performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_add_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.App.TAG_NAV_ICON_BUTTON).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_close_nav_icon
            )
        ).assertIsDisplayed()
    }

    @Test
    fun When_On_NewDog_TopBar_Action_Icon_Is_Settings() {
        FakeOnboardingDataSourceSingleton.onboarding = MutableStateFlow(OnboardingState.Onboarded)
        composeTestRule.onNodeWithTag(TestTags.DogList.TAG_ADD_DOG_BUTTON).performClick()
        composeTestRule.onNodeWithText(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.title_add_dog
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.App.TAG_ACTION_ICON_BUTTON).assertExists()
        composeTestRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(
                R.string.cd_settings_action_icon
            )
        ).assertIsDisplayed()
    }
}
