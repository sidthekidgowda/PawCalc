package com.sidgowda.pawcalc.doginput

import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.doginput.model.DogInputRequirements
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.doginput.model.updateName
import com.sidgowda.pawcalc.doginput.model.updateWeight
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DogInputRequirementsTest {

    private lateinit var dogInputState: MutableStateFlow<DogInputState>
    private lateinit var scope: TestScope
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        dogInputState = MutableStateFlow(DogInputState(isLoading = false))
        testDispatcher = UnconfinedTestDispatcher()
        scope = TestScope(testDispatcher)
    }

    @Test
    fun `when name is under 50 characters, it is valid`() {
        dogInputState.updateName("Hello")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            name = "Hello",
            inputRequirements = setOf(DogInputRequirements.NameBetweenZeroAndFifty)
        )
    }

    @Test
    fun `when name is empty, name is still valid but input requirements are not met`() {
        dogInputState.updateName("")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            name = "",
            inputRequirements = emptySet()
        )
    }

    @Test
    fun `when name is more than 50 characters, it is invalid`() {
        var nameWith60 = ""
        val numToString = { (0..9).map { it.toString() }.joinToString(separator = "") }
        repeat(6) {
            nameWith60 += numToString()
        }
        dogInputState.updateName(nameWith60)

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            name = nameWith60,
            isNameValid = false
        )
    }

    @Test
    fun `when name is valid and then changed to invalid, no input requirements should be met`() {
        var nameWith60 = ""
        val numToString = { (0..9).map { it.toString() }.joinToString(separator = "") }
        repeat(6) {
            nameWith60 += numToString()
        }
        val history = createStateHistory()
        dogInputState.updateName("Hello")
        dogInputState.updateName(nameWith60)

        history shouldContainExactly listOf(
            DogInputState(isLoading = false),
            DogInputState(
                isLoading = false,
                name = "Hello",
                inputRequirements = setOf(DogInputRequirements.NameBetweenZeroAndFifty)
            ),
            DogInputState(
                isLoading = false,
                name = nameWith60,
                isNameValid = false,
                inputRequirements = emptySet()
            )
        )
    }

    @Test
    fun `when name is invalid and then changed to valid, then input requirements should be met`() {
        var nameWith60 = ""
        val numToString = { (0..9).map { it.toString() }.joinToString(separator = "") }
        repeat(6) {
            nameWith60 += numToString()
        }
        val history = createStateHistory()
        dogInputState.updateName(nameWith60)
        dogInputState.updateName("Hello")

        history shouldContainExactly listOf(
            DogInputState(isLoading = false),
            DogInputState(
                isLoading = false,
                name = nameWith60,
                isNameValid = false,
                inputRequirements = emptySet()
            ),
            DogInputState(
                isLoading = false,
                name = "Hello",
                inputRequirements = setOf(DogInputRequirements.NameBetweenZeroAndFifty)
            )
        )
    }

    @Test
    fun `when weight is in lbs and under 500, then it is valid`() {
        dogInputState.updateWeight("50")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            weight = "50",
            inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
        )
    }

    @Test
    fun `when weight is in lbs and more than 500, then it is invalid`() {
        dogInputState.updateWeight("5000")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            weight = "5000",
            isWeightValid = false,
            inputRequirements = emptySet()
        )
    }

    @Test
    fun `when weight is in lbs and equal to 500, then it is valid`() {
        dogInputState.updateWeight("500.0")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            weight = "500.0",
            isWeightValid = true,
            inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
        )
    }

    @Test
    fun `when weight is in lbs and less than zero, then it is invalid`() {
        dogInputState.updateWeight("-5")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            weight = "-5",
            isWeightValid = false,
            inputRequirements = emptySet()
        )
    }

    @Test
    fun `when weight is in lbs and not a valid number than zero, then it is invalid`() {
        dogInputState.updateWeight("55.55.5")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            weight = "55.55.5",
            isWeightValid = false,
            inputRequirements = emptySet()
        )
    }

    @Test
    fun `when weight is in kg and less than 225kg, then it is valid`() {
        dogInputState.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }
        dogInputState.updateWeight("100.0")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            weight = "100.0",
            weightFormat = WeightFormat.KILOGRAMS,
            isWeightValid = true,
            inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
        )
    }

    @Test
    fun `when weight is in kg and equal to 225kg, then it is valid`() {
        dogInputState.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }
        dogInputState.updateWeight("225.0")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            weight = "225.0",
            weightFormat = WeightFormat.KILOGRAMS,
            isWeightValid = true,
            inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
        )
    }

    @Test
    fun `when weight is in kg and more than 225kg, then it is invalid`() {
        dogInputState.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }
        dogInputState.updateWeight("500")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            weight = "500",
            weightFormat = WeightFormat.KILOGRAMS,
            isWeightValid = false,
            inputRequirements = emptySet()
        )
    }

    @Test
    fun `when weight is in kg and less than 0kg, then it is invalid`() {
        dogInputState.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }
        dogInputState.updateWeight("-100")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            weight = "-100",
            weightFormat = WeightFormat.KILOGRAMS,
            isWeightValid = false,
            inputRequirements = emptySet()
        )
    }

    @Test
    fun `when weight is in kg and not a number, then it is invalid`() {
        dogInputState.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }
        dogInputState.updateWeight("100.2.22")

        dogInputState.value shouldBe DogInputState(
            isLoading = false,
            weight = "100.2.22",
            weightFormat = WeightFormat.KILOGRAMS,
            isWeightValid = false,
            inputRequirements = emptySet()
        )
    }

    private fun createStateHistory(): List<DogInputState> {
        val history = mutableListOf<DogInputState>()
        scope.backgroundScope.launch {
            dogInputState.toCollection(history)
        }
        return history
    }

}
