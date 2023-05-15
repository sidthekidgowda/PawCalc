package com.sidgowda.pawcalc

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.data.modules.OnboardingDataModule
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepoImpl
import com.sidgowda.pawcalc.db.di.DbModule
import com.sidgowda.pawcalc.navigation.DOG_LIST_SCREEN_ROUTE
import com.sidgowda.pawcalc.navigation.NEW_DOG_SCREEN_ROUTE
import com.sidgowda.pawcalc.test.IdlingResourceCoroutineDispatcher
import com.sidgowda.pawcalc.test.TestTags
import com.sidgowda.pawcalc.test.fakes.FakeOnboardingDataSource
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
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
class NavigationOnboardedTest {

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
    val onboardingDataSource = FakeOnboardingDataSource(OnboardingState.Onboarded)

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
        IdlingRegistry.getInstance().unregister(ioIdlingDispatcher)
        IdlingRegistry.getInstance().unregister(computationIdlingDispatcher)
    }

    @Test
    fun DogList_Displayed_As_Default() {
        composeTestRule.onNodeWithTag(DOG_LIST_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Add_Dog_Button_Navigates_To_New_Dog() {
        composeTestRule.onNodeWithTag(
            TestTags.DogList.TAG_ADD_DOG_BUTTON
        ).performClick()

        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Dog_One_Navigates_To_Edit_Dog() {
//        composeTestRule.onNodeWithTag(
//            TestTags.DogList.TAG_ADD_DOG_BUTTON
//        ).performClick()
//
//        composeTestRule.onNodeWithTag(NEW_DOG_SCREEN_ROUTE).assertIsDisplayed()
    }

    @Test
    fun Clicking_On_Settings_Icon_Navigates_To_Settings_Screen() {

    }

}
