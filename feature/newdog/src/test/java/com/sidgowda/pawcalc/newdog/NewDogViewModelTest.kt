package com.sidgowda.pawcalc.newdog

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.data.dogs.model.DogInput
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputRequirements
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.domain.dogs.AddDogUseCase
import com.sidgowda.pawcalc.newdog.ui.NewDogViewModel
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class NewDogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var scope: TestScope
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: NewDogViewModel
    private lateinit var addDogUseCase: AddDogUseCase
    private lateinit var capturedDog: CapturingSlot<DogInput>

    @Before
    fun setup() {
        addDogUseCase = mockk()
        capturedDog = slot()
        coEvery { addDogUseCase.invoke(capture(capturedDog)) } returns Unit
        testDispatcher = StandardTestDispatcher()
        viewModel = NewDogViewModel(addDogUseCase, testDispatcher)
        scope = TestScope()
    }

    @Test
    fun `given viewModel is initialized, then it should have an initial state`() {
        val history = viewModel.createStateHistory()

        history shouldContainExactly listOf(
            INITIAL_STATE
        )
    }

    @Test
    fun `given initial dog input state, when event updates name, then state should have name and input requirements being updated`() {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.NameChanged("Hello"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                name = "Hello",
                inputRequirements = setOf(DogInputRequirements.NameBetweenZeroAndFifty)
            )
        )
    }

    @Test
    fun `when name is has 50 characters, then state should have isNameNotValid set to true and input requirements being updated`() {
        val history = viewModel.createStateHistory()
        var nameWith50 = ""
        val numToString = { (0..9).map { it.toString() }.joinToString(separator = "") }
        repeat(5) {
            nameWith50 += numToString()
        }
        viewModel.handleEvent(DogInputEvent.NameChanged(nameWith50))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                name = nameWith50,
                inputRequirements = setOf(DogInputRequirements.NameBetweenZeroAndFifty)
            )
        )
    }

    @Test
    fun `when name is more than 50 characters, then state should have isNameNotValid set to false and input requirements being empty`() {
        val history = viewModel.createStateHistory()
        var nameWith60 = ""
        val numToString = { (0..9).map { it.toString() }.joinToString(separator = "") }
        repeat(6) {
            nameWith60 += numToString()
        }
        viewModel.handleEvent(DogInputEvent.NameChanged(nameWith60))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                name = nameWith60,
                inputRequirements = emptySet(),
                isNameValid = false
            )
        )
    }

    @Test
    fun `given name with initial input, when event sets name back to empty string, then state should have isNameValid equal to true`() {
        val history = viewModel.createStateHistory()

        viewModel.handleEvent(DogInputEvent.NameChanged("Hello"))
        // reset name
        viewModel.handleEvent(DogInputEvent.NameChanged(""))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                name = "Hello",
                inputRequirements = setOf(DogInputRequirements.NameBetweenZeroAndFifty)
            ),
            INITIAL_STATE.copy(isNameValid = true)
        )
    }

    @Test
    fun `given name with more than 50 characters, when name is updated to less than 50 characters, then state should have isNameValid equal to true and input requirements being updated`() {
        val history = viewModel.createStateHistory()
        var nameWith60 = ""
        val numToString = { (0..9).map { it.toString() }.joinToString(separator = "") }
        repeat(6) {
            nameWith60 += numToString()
        }
        viewModel.handleEvent(DogInputEvent.NameChanged(nameWith60))
        // reset name
        var nameWith40 = ""
        repeat(4) {
            nameWith40 += numToString()
        }
        viewModel.handleEvent(DogInputEvent.NameChanged(nameWith40))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                name = nameWith60,
                isNameValid = false
            ),
            DogInputState(
                name = nameWith40,
                isNameValid = true,
                inputRequirements = setOf(DogInputRequirements.NameBetweenZeroAndFifty)
            )
        )
    }
    @Test
    fun `when event updates weight, then state should have weight and input requirements being updated`() {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.WeightChanged("50"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                weight = "50",
                inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500)
            )
        )
    }

    @Test
    fun `when weight is not a number, then state should have isWeightValid set to false`() {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.WeightChanged("50.50.50"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                weight = "50.50.50",
                isWeightValid = false
            )
        )
    }

    @Test
    fun `when weight is less than 0, then state should have isWeightValid set to false`() {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.WeightChanged("-60.1"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                weight = "-60.1",
                isWeightValid = false
            )
        )
    }

    @Test
    fun `when weight is a valid number, then state should have input requirements being updated`() {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.WeightChanged("100"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                weight = "100",
                inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500)
            )
        )
    }

    @Test
    fun `given weight is valid, when weight is set to an invalid number, then state should have isWeightValid set to false and input requirements being empty`() {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.WeightChanged("100"))
        viewModel.handleEvent(DogInputEvent.WeightChanged("100.25-23"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                weight = "100",
                inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500)
            ),
            DogInputState(
                weight = "100.25-23",
                isWeightValid = false
            )
        )
    }

    @Test
    fun `given birth date is valid, then state should have birthDate being set and input requirements being updated`() {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.BirthDateDialogShown)
        viewModel.handleEvent(DogInputEvent.BirthDateChanged("12/20/1990"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                birthDateDialogShown = true,
            ),
            DogInputState(
                birthDate = "12/20/1990",
                birthDateDialogShown = true,
                inputRequirements = setOf(DogInputRequirements.BirthDate)
            )
        )
    }

    @Test
    fun `given birth date dialog shown, when birth date is empty, then isBirthDateValid should be set to false`() {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.BirthDateDialogShown)
        viewModel.handleEvent(DogInputEvent.BirthDateChanged(""))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                birthDateDialogShown = true,
            ),
            DogInputState(
                birthDateDialogShown = true,
                birthDate = "",
                isBirthDateValid = false
            )
        )
    }

    @Test
    fun `given birth date invalid, when birth date is updated, then isBirthDateValid should be set to true and input requirements updated`() {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.BirthDateDialogShown)
        viewModel.handleEvent(DogInputEvent.BirthDateChanged(""))
        viewModel.handleEvent(DogInputEvent.BirthDateChanged("7/30/2019"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                birthDateDialogShown = true,
            ),
            DogInputState(
                birthDateDialogShown = true,
                birthDate = "",
                isBirthDateValid = false
            ),
            DogInputState(
                birthDateDialogShown = true,
                isBirthDateValid = true,
                birthDate = "7/30/2019",
                inputRequirements = setOf(DogInputRequirements.BirthDate)
            )
        )
    }

    @Test
    fun `when profile pic is updated, then input requirements should be updated`() {
        val history = viewModel.createStateHistory()
        val uri = "http://pic".toUri()
        viewModel.handleEvent(DogInputEvent.PicChanged(uri))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = uri,
                inputRequirements = setOf(DogInputRequirements.OnePicture)
            )
        )
    }

    @Test
    fun `when input requirements are met, then state should be valid`() {
        val uri = "http://pic".toUri()
        viewModel.handleEvent(DogInputEvent.PicChanged(uri))
        viewModel.handleEvent(DogInputEvent.NameChanged("Mowgli"))
        viewModel.handleEvent(DogInputEvent.WeightChanged("100"))
        viewModel.handleEvent(DogInputEvent.BirthDateChanged("7/30/2019"))

        viewModel.inputState.value.isInputValid().shouldBeTrue()
    }

    @Test
    fun `given input requirements are valid, when weight is changed to make input requirements invalid, then state should be invalid`() {
        val uri = "http://pic".toUri()
        viewModel.handleEvent(DogInputEvent.PicChanged(uri))
        viewModel.handleEvent(DogInputEvent.NameChanged("Mowgli"))
        viewModel.handleEvent(DogInputEvent.WeightChanged("100"))
        viewModel.handleEvent(DogInputEvent.BirthDateChanged("7/30/2019"))
        viewModel.inputState.value.isInputValid().shouldBeTrue()

        viewModel.handleEvent(DogInputEvent.WeightChanged("100. 00 . "))

        viewModel.inputState.value.isInputValid().shouldBeFalse()
    }

    @Test
    fun `given input requirements are valid, when saveDog is called, verify addUseCase is invoked`() = scope.runTest {
        val dogInput = dogInput()
        viewModel.inputState.value.isInputValid().shouldBeTrue()

        // all input is valid
        viewModel.handleEvent(DogInputEvent.SavingInfo)
        advanceUntilIdle()

        coVerify { addDogUseCase.invoke(dogInput) }
        capturedDog.captured shouldBe dogInput
    }

    private fun dogInput(): DogInput {
        val uri = "http://pic".toUri()
        viewModel.handleEvent(DogInputEvent.PicChanged(uri))
        viewModel.handleEvent(DogInputEvent.NameChanged("Mowgli"))
        viewModel.handleEvent(DogInputEvent.WeightChanged("100"))
        viewModel.handleEvent(DogInputEvent.BirthDateChanged("7/30/2019"))

        return DogInput(
            profilePic = uri,
            name = "Mowgli",
            weight = "100",
            birthDate = "7/30/2019"
        )

    }

    private fun NewDogViewModel.createStateHistory(): List<DogInputState> {
        val history = mutableListOf<DogInputState>()
        scope.backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            inputState.toCollection(history)
        }
        return history
    }

    private companion object {
        private val INITIAL_STATE = DogInputState()
    }
}

