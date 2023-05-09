package com.sidgowda.pawcalc.editdog

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.formattedToString
import com.sidgowda.pawcalc.data.dogs.model.formattedToTwoDecimals
import com.sidgowda.pawcalc.data.dogs.model.toNewWeight
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputRequirements
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.domain.dogs.GetDogForIdUseCase
import com.sidgowda.pawcalc.domain.dogs.UpdateDogUseCase
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import com.sidgowda.pawcalc.editdog.ui.EditDogViewModel
import com.sidgowda.pawcalc.test.MainDispatcherRule
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.flow.update
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
    private lateinit var getSettingsUseCase: GetSettingsUseCase
    private lateinit var computationTestDispatcher: TestDispatcher
    private lateinit var viewModel: EditDogViewModel
    private lateinit var getDogForIdUseCase: GetDogForIdUseCase
    private lateinit var updateDogUseCase: UpdateDogUseCase
    private lateinit var capturedDog: CapturingSlot<Dog>
    private lateinit var settingsFlow: MutableStateFlow<Settings>
    private lateinit var savedStateHandle: SavedStateHandle
    private val dogs = listOf(DOG_ONE, DOG_TWO, DOG_THREE)

    @Before
    fun setup() {
        updateDogUseCase = mockk()
        getSettingsUseCase = mockk()
        getDogForIdUseCase = mockk()
        capturedDog = slot()
        settingsFlow = MutableStateFlow(DEFAULT_SETTINGS)
        every { getSettingsUseCase.invoke() } returns settingsFlow
        coEvery { updateDogUseCase.invoke(capture(capturedDog)) } returns Unit
        coEvery { getDogForIdUseCase.invoke(any()) } answers {
            val id = firstArg<Int>()
            if (id > 3) {
                throw IllegalArgumentException("There are only 3 items in list")
            } else {
                flowOf(dogs[id-1])
            }
        }
        savedStateHandle = mockk(relaxed = true)
        ioTestDispatcher = StandardTestDispatcher()
        computationTestDispatcher = StandardTestDispatcher()
        scope = TestScope()
    }

    @Test
    fun `given list of dogs, when getDogForId is called with id one, then viewModel should be initialized with Dog One`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
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
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
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
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `when name is updated then input state is updated as well`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        viewModel.handleEvent(DogInputEvent.NameChanged("New name"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
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
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        var nameWith60 = ""
        val numToString = { (0..9).map { it.toString() }.joinToString(separator = "") }
        repeat(6) {
            nameWith60 += numToString()
        }

        viewModel.handleEvent(DogInputEvent.NameChanged(nameWith60))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = nameWith60,
                isNameValid = false,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = setOf(
                    DogInputRequirements.BirthDate,
                    DogInputRequirements.OnePicture,
                    DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg
                )
            )
        )
        viewModel.dogInputState.value.isInputValid().shouldBeFalse()
    }

    @Test
    fun `when birthdate is updated then input state is updated as well`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        updateDate("12/20/2021")

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDateDialogShown = false,
                birthDate = "12/20/2021",
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = "12/20/2021",
                birthDateDialogShown = true,
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `when birthdate is an empty string, then input state is invalid`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        updateDate("")

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = "",
                birthDateDialogShown = false,
                inputRequirements = setOf(
                    DogInputRequirements.NameBetweenZeroAndFifty,
                    DogInputRequirements.OnePicture,
                    DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg
                )
            ),
            DogInputState(
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = "",
                isBirthDateValid = false,
                birthDateDialogShown = true,
                inputRequirements = setOf(
                    DogInputRequirements.NameBetweenZeroAndFifty,
                    DogInputRequirements.OnePicture,
                    DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg
                )
            )
        )
        viewModel.dogInputState.value.isInputValid().shouldBeFalse()
    }

    @Test
    fun `when weight is changed, then input state is updated`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        viewModel.handleEvent(DogInputEvent.WeightChanged("80.0"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_ONE.profilePic,
                name = DOG_ONE.name,
                weight = DOG_ONE.weight.toString(),
                birthDate = DOG_ONE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
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
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        viewModel.handleEvent(DogInputEvent.WeightChanged("-50.0"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
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
        viewModel.dogInputState.value.isInputValid().shouldBeFalse()
    }

    @Test
    fun `when weight is not a number, then input state is invalid`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        viewModel.handleEvent(DogInputEvent.WeightChanged("1.23.0"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
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
        viewModel.dogInputState.value.isInputValid().shouldBeFalse()
    }

    @Test
    fun `when weight is more than 500, then input state is invalid`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        viewModel.handleEvent(DogInputEvent.WeightChanged("5000"))

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
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
        viewModel.dogInputState.value.isInputValid().shouldBeFalse()
    }

    @Test
    fun `when all updates are valid, then isInputValid should be true`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel().also { advanceUntilIdle() }

        viewModel.handleEvent(DogInputEvent.WeightChanged("29"))
        updateDate("2/2/2021")
        viewModel.handleEvent(DogInputEvent.NameChanged("Mowgs"))
        viewModel.handleEvent(DogInputEvent.PicChanged("http://newpic".toUri()))

        viewModel.dogInputState.value.isInputValid().shouldBeTrue()
    }

    @Test
    fun `when save dog is called and nothing is updated, verify updateUseCase uses sameDog`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel().also { advanceUntilIdle() }

        viewModel.handleEvent(DogInputEvent.SavingInfo).also { advanceUntilIdle() }

        coVerify { updateDogUseCase.invoke(DOG_THREE) }
        capturedDog.captured shouldBe DOG_THREE
    }

    @Test
    fun `when save dog is called, verify updateUseCase is invoked with updatedDog`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel().also { advanceUntilIdle() }
        val updatedDog = updatedDog()

        viewModel.handleEvent(DogInputEvent.SavingInfo)
        advanceUntilIdle()

        coVerify { updateDogUseCase.invoke(updatedDog) }
        capturedDog.captured shouldBe updatedDog
    }

    @Test
    fun `when weight input has 4 decimals and save dog is called, it is formatted to 2 decimals`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel().also { advanceUntilIdle() }
        viewModel.handleEvent(DogInputEvent.WeightChanged("73.289222"))

        viewModel.handleEvent(DogInputEvent.SavingInfo).also { advanceUntilIdle() }

        val expectedDog = DOG_ONE.copy(
            weight = 73.29
        )
        coVerify { updateDogUseCase.invoke(expectedDog) }
        capturedDog.captured shouldBe expectedDog
    }

    @Test
    fun `when weight is changed to kilograms, weight input should be changed to kilograms`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 3
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }

        //change to kilograms
        settingsFlow.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }.also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toString(),
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                profilePic = DOG_THREE.profilePic,
                name = DOG_THREE.name,
                weight = DOG_THREE.weight.toNewWeight(WeightFormat.KILOGRAMS).formattedToString(),
                weightFormat = WeightFormat.KILOGRAMS,
                birthDate = DOG_THREE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `when weight is changed to kilograms and changed back to lbs, weight input should be in lbs`() =
        scope.runTest {
            every { savedStateHandle.get<Int>("dogId") } returns 3
            initializeViewModel()
            val history = viewModel.createStateHistory().also { advanceUntilIdle() }

            //change to kilograms
            settingsFlow.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }
                .also { advanceUntilIdle() }
            settingsFlow.update { it.copy(weightFormat = WeightFormat.POUNDS) }
                .also { advanceUntilIdle() }

            history shouldContainExactly listOf(
                INITIAL_STATE,
                DogInputState(
                    profilePic = DOG_THREE.profilePic,
                    name = DOG_THREE.name,
                    weight = DOG_THREE.weight.toString(),
                    birthDate = DOG_THREE.birthDate,
                    inputRequirements = DogInputRequirements.values().toSet()
                ),
                DogInputState(
                    profilePic = DOG_THREE.profilePic,
                    name = DOG_THREE.name,
                    weight = DOG_THREE.weight.toNewWeight(WeightFormat.KILOGRAMS)
                        .formattedToString(),
                    weightFormat = WeightFormat.KILOGRAMS,
                    birthDate = DOG_THREE.birthDate,
                    inputRequirements = DogInputRequirements.values().toSet()
                ),
                DogInputState(
                    profilePic = DOG_THREE.profilePic,
                    name = DOG_THREE.name,
                    weight = DOG_THREE.weight.toNewWeight(WeightFormat.KILOGRAMS)
                        .formattedToTwoDecimals().toNewWeight(WeightFormat.POUNDS)
                        .formattedToString(),
                    birthDate = DOG_THREE.birthDate,
                    inputRequirements = DogInputRequirements.values().toSet()
                )
            )
        }

    @Test
    fun `when weight is invalid in lbs and changed to kilograms, then it is invalid in kilograms`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        viewModel.handleEvent(DogInputEvent.WeightChanged("8500"))

        // change to kilograms
        settingsFlow.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }.also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = "8500",
                isWeightValid = false,
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
                    .minus(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
            ),
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                isWeightValid = false,
                weightFormat = WeightFormat.KILOGRAMS,
                weight = "8500".toDouble().toNewWeight(WeightFormat.KILOGRAMS).formattedToString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
                    .minus(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
            )
        )
    }

    @Test
    fun `when weight is not a number in lbs and changed to kilograms, then it is not converted in kilograms`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }
        viewModel.handleEvent(DogInputEvent.WeightChanged("5.5.5"))

        //change to kilograms
        settingsFlow.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }.also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = DOG_TWO.weight.toString(),
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                weight = "5.5.5",
                isWeightValid = false,
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
                    .minus(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
            ),
            DogInputState(
                profilePic = DOG_TWO.profilePic,
                name = DOG_TWO.name,
                isWeightValid = false,
                weightFormat = WeightFormat.KILOGRAMS,
                weight = "5.5.5",
                birthDate = DOG_TWO.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
                    .minus(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
            )
        )
    }

    @Test
    fun `when date format is changed to international, then date input is changed to days first`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }

        // change date to international
        settingsFlow.update { it.copy(dateFormat = DateFormat.INTERNATIONAL) }.also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_ONE.profilePic,
                name = DOG_ONE.name,
                weight = DOG_ONE.weight.toString(),
                birthDate = DOG_ONE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                profilePic = DOG_ONE.profilePic,
                name = DOG_ONE.name,
                weight = DOG_ONE.weight.toString(),
                birthDate = "30/7/2019",
                dateFormat = DateFormat.INTERNATIONAL,
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `when date format is changed to international and back to american, then date input is in months first`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 1
        initializeViewModel()
        val history = viewModel.createStateHistory().also { advanceUntilIdle() }

        // change date to international
        settingsFlow.update { it.copy(dateFormat = DateFormat.INTERNATIONAL) }.also { advanceUntilIdle() }
        // change back to american
        settingsFlow.update { it.copy(dateFormat = DateFormat.AMERICAN) }.also { advanceUntilIdle() }

        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                profilePic = DOG_ONE.profilePic,
                name = DOG_ONE.name,
                weight = DOG_ONE.weight.toString(),
                birthDate = DOG_ONE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                profilePic = DOG_ONE.profilePic,
                name = DOG_ONE.name,
                weight = DOG_ONE.weight.toString(),
                birthDate = "30/7/2019",
                dateFormat = DateFormat.INTERNATIONAL,
                inputRequirements = DogInputRequirements.values().toSet()
            ),
            DogInputState(
                profilePic = DOG_ONE.profilePic,
                name = DOG_ONE.name,
                weight = DOG_ONE.weight.toString(),
                birthDate = DOG_ONE.birthDate,
                inputRequirements = DogInputRequirements.values().toSet()
            )
        )
    }

    @Test
    fun `verify save dog is called with settings date format international and kilograms format`() = scope.runTest {
        every { savedStateHandle.get<Int>("dogId") } returns 2
        initializeViewModel().also { advanceUntilIdle() }
        settingsFlow.update {
            it.copy(
                weightFormat = WeightFormat.KILOGRAMS,
                dateFormat = DateFormat.INTERNATIONAL
            )
        }.also { advanceUntilIdle() }

        viewModel.handleEvent(DogInputEvent.SavingInfo).also { advanceUntilIdle() }

        val expectedDog = DOG_TWO.copy(
            birthDate = "15/4/2019",
            dateFormat = DateFormat.INTERNATIONAL,
            weight = 85.0.toNewWeight(WeightFormat.KILOGRAMS).formattedToTwoDecimals(),
            weightFormat = WeightFormat.KILOGRAMS
        )
        coVerify { updateDogUseCase.invoke(expectedDog) }
        capturedDog.captured shouldBe expectedDog
    }

    private fun updateDate(date: String) {
        viewModel.handleEvent(DogInputEvent.BirthDateChanged(date))
        viewModel.handleEvent(DogInputEvent.BirthDateDialogShown)
    }

    private fun updatedDog(): Dog {
        val uri = "http://pic".toUri()
        viewModel.handleEvent(DogInputEvent.PicChanged(uri))
        viewModel.handleEvent(DogInputEvent.NameChanged("Mowgli"))
        viewModel.handleEvent(DogInputEvent.WeightChanged("100.0"))
        updateDate("7/30/2019")

        return Dog(
            id = 2,
            name = "Mowgli",
            weight = 100.0,
            profilePic = uri,
            birthDate = "7/30/2019",
            dogYears = "7/30/2019".toDogYears(),
            humanYears = "7/30/2019".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
    }

    private fun initializeViewModel() {
        viewModel = EditDogViewModel(
            getDogForIdUseCase = getDogForIdUseCase,
            settingsUseCase = getSettingsUseCase,
            savedStateHandle = savedStateHandle,
            updateDogUseCase = updateDogUseCase,
            ioDispatcher = ioTestDispatcher,
            computationDispatcher = computationTestDispatcher,
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
            humanYears = "7/30/2019".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
        private val DOG_TWO = Dog(
            id = 2,
            name = "Tucker",
            profilePic = Uri.parse("http://image2"),
            birthDate = "4/15/2019",
            weight = 85.0,
            dogYears = "4/15/2019".toDogYears(),
            humanYears = "4/15/2019".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
        private val DOG_THREE = Dog(
            id = 3,
            name = "Todd",
            profilePic = Uri.parse("http://image2"),
            birthDate = "12/1/2022",
            weight = 65.0,
            dogYears = "12/1/2022".toDogYears(),
            humanYears = "12/1/2022".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
        private val INITIAL_STATE = DogInputState()
        private val DEFAULT_SETTINGS = Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
    }
}
