package com.sidgowda.pawcalc.editdog

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.doginput.model.DogInputRequirements
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.domain.GetDogForIdUseCase
import com.sidgowda.pawcalc.domain.UpdateDogUseCase
import com.sidgowda.pawcalc.editdog.ui.EditDogViewModel
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class EditDogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var scope: TestScope
    private lateinit var ioTestDispatcher: TestDispatcher
    private lateinit var computationTestDispatcher: TestDispatcher
    private lateinit var viewModel: EditDogViewModel
    private lateinit var getDogForIdUseCase: GetDogForIdUseCase
    private lateinit var updateDogUseCase: UpdateDogUseCase
    private lateinit var capturedDog: CapturingSlot<Dog>
    private val dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE)

    @Before
    fun setup() {
        updateDogUseCase = mockk()
        getDogForIdUseCase = mockk()
        capturedDog = slot()
        coEvery { updateDogUseCase.invoke(capture(capturedDog)) } returns Unit
        coEvery { getDogForIdUseCase.invoke(any()) } answers {
            val id = firstArg<Int>()
            if (id > 3) {
                throw IllegalArgumentException("There are only 3 items in list")
            } else {
                flowOf(dogs[id-1])
            }
        }
        ioTestDispatcher = StandardTestDispatcher()
        computationTestDispatcher = StandardTestDispatcher()
        viewModel = EditDogViewModel(
            getDogForIdUseCase = getDogForIdUseCase,
            updateDogUseCase = updateDogUseCase,
            ioDispatcher = ioTestDispatcher,
            computationDispatcher = computationTestDispatcher
        )
        scope = TestScope()
    }

    @Test
    fun `given viewModel is initialized, then it should have an initial state with loading`() {
        val history = viewModel.createStateHistory()

        history shouldContainExactly listOf(
            INITIAL_STATE
        )
    }

    @Test
    fun `given list of dogs, when getDogForId is called with id, then viewModel should be initialized with Dog One`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(1)
        advanceUntilIdle()

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_ONE.profilePic,
                name = DOG_ONE.name,
                weight = DOG_ONE.weight.toString(),
                birthDate = DOG_ONE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `given list of dogs, when getDogForId is called with id 2, then viewModel should be initialized with Dog Two`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(2)
        advanceUntilIdle()

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `given list of dogs, when getDogForId is called with id 3, then viewModel should be initialized with Dog Three`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(3)
        advanceUntilIdle()

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `when getDogForId is called with an invalid id then viewModel should emit an error`() = scope.runTest {
        val history = viewModel.createStateHistory()
        // only ids 1,2,3 exist in list, id 4 does not exist
        viewModel.fetchDogForId(4)
        advanceUntilIdle()

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                isError = true
            )
        )
    }

    // see name changed when saving
    // see birth date changed when saving
    // see weight changed when saving
    // see requirements change to invalid for name
    // see requirements change to invalid for weight
    // see requirements change to invalid for birthdate
    // see requirements change to invalid for profilepic

    private fun EditDogViewModel.createStateHistory(): List<DogInputState> {
        val history = mutableListOf<DogInputState>()
        scope.backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            dogInputState.toCollection(history)
        }
        return history
    }

    private companion object {
        private val DOG_ONE = Dog(
            id = 1,
            name = "Mowgli",
            profilePic = Uri.parse("http://image1"),
            birthDate = "7/30/2019",
            weight = 75.0,
            dogYears = "7/30/2019".toDogYears(),
            humanYears = "7/30/2019".toHumanYears()
        )
        private val DOG_TWO = Dog(
            id = 2,
            name = "Tucker",
            profilePic = Uri.parse("http://image2"),
            birthDate = "4/15/2019",
            weight = 85.0,
            dogYears = "4/15/2019".toDogYears(),
            humanYears = "4/15/2019".toHumanYears()
        )
        private val DOG_THREE = Dog(
            id = 3,
            name = "Todd",
            profilePic = Uri.parse("http://image2"),
            birthDate = "12/1/2022",
            weight = 65.0,
            dogYears = "12/1/2022".toDogYears(),
            humanYears = "12/1/2022".toHumanYears()
        )
        private val INITIAL_STATE = DogInputState(
            isLoading = true
        )
    }
}
