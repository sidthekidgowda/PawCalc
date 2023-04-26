package com.sidgowda.pawcalc.editdog

import android.net.Uri
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputRequirements
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.domain.GetDogForIdUseCase
import com.sidgowda.pawcalc.domain.UpdateDogUseCase
import com.sidgowda.pawcalc.editdog.ui.EditDogViewModel
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
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
    fun `given list of dogs, when getDogForId is called with id one, then viewModel should be initialized with Dog One`() = scope.runTest {
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
    fun `given list of dogs, when getDogForId is called with id two, then viewModel should be initialized with Dog Two`() = scope.runTest {
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
    fun `given list of dogs, when getDogForId is called with id three, then viewModel should be initialized with Dog Three`() = scope.runTest {
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

    @Test
    fun `when name is updated then input state is updated as well`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(3)
        advanceUntilIdle()
        viewModel.handleEvent(DogInputEvent.NameChanged("New name"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                isLoading = false,
                profilePic = DOG_THREE.profilePic,
                name = "New name",
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `when name is updated and is more than 50characters then input state is invalid`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(2)
        advanceUntilIdle()
        var nameWith60 = ""
        val numToString = { (0..9).map { it.toString() }.joinToString(separator = "") }
        repeat(6) {
            nameWith60 += numToString()
        }

        viewModel.handleEvent(DogInputEvent.NameChanged(nameWith60))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                isLoading = false,
                profilePic = DOG_TWO.profilePic,
                name = nameWith60,
                isNameValid = false,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = setOf(
                    DogInputRequirements.BirthDate,
                    DogInputRequirements.OnePicture,
                    DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500
                )
            )
        )
        viewModel.dogInputState.value.isInputValid() shouldBe false
    }

    @Test
    fun `when birthdate is updated then input state is updated as well`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(3)
        advanceUntilIdle()
        viewModel.handleEvent(DogInputEvent.BirthDateDialogShown)
        viewModel.handleEvent(DogInputEvent.BirthDateChanged("12/20/2021"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                isLoading = false,
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                birthDateDialogShown = true,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                isLoading = false,
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDateDialogShown = true,
                birthDate = "12/20/2021",
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `when birthdate is an empty string, then input state is invalid`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(3)
        advanceUntilIdle()
        viewModel.handleEvent(DogInputEvent.BirthDateDialogShown)
        viewModel.handleEvent(DogInputEvent.BirthDateChanged(""))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                isLoading = false,
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                birthDateDialogShown = true,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                isLoading = false,
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = "",
                isBirthDateValid = false,
                birthDateDialogShown = true,
                inputRequirements = setOf(
                    DogInputRequirements.NameBetweenZeroAndFifty,
                    DogInputRequirements.OnePicture,
                    DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500
                )
            )
        )
        viewModel.dogInputState.value.isInputValid() shouldBe false
    }

    @Test
    fun `when weight is changed, then input state is updated`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(1)
        advanceUntilIdle()
        viewModel.handleEvent(DogInputEvent.WeightChanged("80.0"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_ONE.profilePic,
                name = DOG_ONE.name,
                weight = DOG_ONE.weight.toString(),
                birthDate = DOG_ONE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                isLoading = false,
                profilePic = DOG_ONE.profilePic,
                name = DOG_ONE.name,
                weight = "80.0",
                birthDate = DOG_ONE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `when weight is updated to less than zero, then input state is invalid`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(2)
        advanceUntilIdle()
        viewModel.handleEvent(DogInputEvent.WeightChanged("-50.0"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                isLoading = false,
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = "-50.0",
                isWeightValid = false,
                birthDate = DOG_TWO.birthDate,
                inputRequirements = setOf(
                    DogInputRequirements.BirthDate,
                    DogInputRequirements.OnePicture,
                    DogInputRequirements.NameBetweenZeroAndFifty
                )
            )
        )
        viewModel.dogInputState.value.isInputValid() shouldBe false
    }

    @Test
    fun `when weight is not a number, then input state is invalid`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(2)
        advanceUntilIdle()
        viewModel.handleEvent(DogInputEvent.WeightChanged("1.23.0"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                isLoading = false,
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = "1.23.0",
                isWeightValid = false,
                birthDate = DOG_TWO.birthDate,
                inputRequirements = setOf(
                    DogInputRequirements.BirthDate,
                    DogInputRequirements.OnePicture,
                    DogInputRequirements.NameBetweenZeroAndFifty
                )
            )
        )
        viewModel.dogInputState.value.isInputValid() shouldBe false
    }

    @Test
    fun `when weight is more than 500, then input state is invalid`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.fetchDogForId(2)
        advanceUntilIdle()
        viewModel.handleEvent(DogInputEvent.WeightChanged("5000"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                isLoading = false,
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                isLoading = false,
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = "5000",
                isWeightValid = false,
                birthDate = DOG_TWO.birthDate,
                inputRequirements = setOf(
                    DogInputRequirements.BirthDate,
                    DogInputRequirements.OnePicture,
                    DogInputRequirements.NameBetweenZeroAndFifty
                )
            )
        )
        viewModel.dogInputState.value.isInputValid() shouldBe false
    }

    // see requirements change to invalid for profilepic
    //
    // add tests for updateBirthDateDialogShown

    @Test
    fun `when save dog is called and nothing is updated, verify updateUseCase uses sameDog`() = scope.runTest {
        viewModel.fetchDogForId(3)
        advanceUntilIdle()

        viewModel.handleEvent(DogInputEvent.SavingInfo)
        advanceUntilIdle()

        coVerify { updateDogUseCase.invoke(DOG_THREE) }
        capturedDog.captured shouldBe DOG_THREE
    }

    @Test
    fun `when save dog is called, verify updateUseCase is invoked with updatedDog`() = scope.runTest {
        viewModel.fetchDogForId(2)
        advanceUntilIdle()
        val updatedDog = updatedDog()

        viewModel.handleEvent(DogInputEvent.SavingInfo)
        advanceUntilIdle()

        coVerify { updateDogUseCase.invoke(updatedDog) }
        capturedDog.captured shouldBe updatedDog
    }

    private fun updatedDog(): Dog {
        val uri = "http://pic".toUri()
        viewModel.handleEvent(DogInputEvent.PicChanged(uri))
        viewModel.handleEvent(DogInputEvent.NameChanged("Mowgli"))
        viewModel.handleEvent(DogInputEvent.WeightChanged("100.0"))
        viewModel.handleEvent(DogInputEvent.BirthDateChanged("7/30/2019"))

        return Dog(
            id = 2,
            name = "Mowgli",
            weight = 100.0,
            profilePic = uri,
            birthDate = "7/30/2019",
            dogYears = "7/30/2019".toDogYears(),
            humanYears = "7/30/2019".toHumanYears()
        )
    }

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
