package com.sidgowda.pawcalc.doglist

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import com.sidgowda.pawcalc.doglist.ui.DogListViewModel
import com.sidgowda.pawcalc.domain.onboarding.GetOnboardingStateUseCase
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DogListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var scope: TestScope
    private lateinit var computationDispatcher: CoroutineDispatcher
    private lateinit var ioDispatcher: CoroutineDispatcher
    private lateinit var dogListViewModel: DogListViewModel
    private lateinit var dogsRepo: DogsRepo
    private lateinit var dogsDataSource: DogsDataSource
    private lateinit var getOnboardingStateUseCase: GetOnboardingStateUseCase

    @Before
    fun setup() {
        ioDispatcher = StandardTestDispatcher()
        computationDispatcher = StandardTestDispatcher()
        dogsDataSource = mockk()
        getOnboardingStateUseCase = mockk()
        dogsRepo = FakeDogsRepo(dogsDataSource)
        dogListViewModel = DogListViewModel(
            getOnboardingState = getOnboardingStateUseCase,
            dogsRepo = dogsRepo,
            ioDispatcher = ioDispatcher,
            computationDispatcher = computationDispatcher
        )
        scope = TestScope()
    }

    // test onboarding state
    // test event fetchDogs - emtpy
    // test event fetchDogs - error
    // test event fetchDogs - dogs
    // test event fetchDogs - error use cache
    // test event navigate to add Dog and local state
    // test event add dog and see if dog is added
    // test event navigate to dog details and local state
    // test event delete dog and see if dog is deleted


}
