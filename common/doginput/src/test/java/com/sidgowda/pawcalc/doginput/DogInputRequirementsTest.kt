package com.sidgowda.pawcalc.doginput

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.doginput.model.*
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
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DogInputRequirementsTest {

    private lateinit var dogInputState: MutableStateFlow<DogInputState>
    private lateinit var scope: TestScope
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        dogInputState = MutableStateFlow(DogInputState())
        testDispatcher = UnconfinedTestDispatcher()
        scope = TestScope(testDispatcher)
    }

    @Test
    fun `when name is under 50 characters, it is valid`() {
        dogInputState.updateName("Hello")

        dogInputState.value shouldBe DogInputState(
            name = "Hello",
            inputRequirements = setOf(DogInputRequirements.NameBetweenZeroAndFifty)
        )
    }

    @Test
    fun `when name is empty, name is still valid but input requirements are not met`() {
        dogInputState.updateName("")

        dogInputState.value shouldBe DogInputState(
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
            DogInputState(),
            DogInputState(
                name = "Hello",
                inputRequirements = setOf(DogInputRequirements.NameBetweenZeroAndFifty)
            ),
            DogInputState(
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
            DogInputState(),
            DogInputState(
                name = nameWith60,
                isNameValid = false,
                inputRequirements = emptySet()
            ),
            DogInputState(
                name = "Hello",
                inputRequirements = setOf(DogInputRequirements.NameBetweenZeroAndFifty)
            )
        )
    }

    @Test
    fun `when weight is in lbs and under 500, then it is valid`() {
        dogInputState.updateWeight("50")

        dogInputState.value shouldBe DogInputState(
            weight = "50",
            inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
        )
    }

    @Test
    fun `when weight is in lbs and more than 500, then it is invalid`() {
        dogInputState.updateWeight("5000")

        dogInputState.value shouldBe DogInputState(
            weight = "5000",
            isWeightValid = false,
            inputRequirements = emptySet()
        )
    }

    @Test
    fun `when weight is in lbs and equal to 500, then it is valid`() {
        dogInputState.updateWeight("500.0")

        dogInputState.value shouldBe DogInputState(
            weight = "500.0",
            isWeightValid = true,
            inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
        )
    }

    @Test
    fun `when weight is in lbs and less than zero, then it is invalid`() {
        dogInputState.updateWeight("-5")

        dogInputState.value shouldBe DogInputState(
            weight = "-5",
            isWeightValid = false,
            inputRequirements = emptySet()
        )
    }

    @Test
    fun `when weight is in lbs and not a valid number than zero, then it is invalid`() {
        dogInputState.updateWeight("55.55.5")

        dogInputState.value shouldBe DogInputState(
            weight = "55.55.5",
            isWeightValid = false,
            inputRequirements = emptySet()
        )
    }

    @Test
    fun `when weight is in lbs and is valid then updated to invalid, no input requirements should be met`() {
        val history = createStateHistory()
        dogInputState.updateWeight("50")
        dogInputState.updateWeight("-500")

        history shouldContainExactly listOf(
            DogInputState(),
            DogInputState(
                weight = "50",
                isWeightValid = true,
                inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
            ),
            DogInputState(
                weight = "-500",
                isWeightValid = false,
                inputRequirements = emptySet()
            )
        )
    }

    @Test
    fun `when weight is in lbs and is invalid then updated to valid, then input requirements should be met`() {
        val history = createStateHistory()
        dogInputState.updateWeight("-500")
        dogInputState.updateWeight("50")

        history shouldContainExactly listOf(
            DogInputState(),
            DogInputState(
                weight = "-500",
                isWeightValid = false,
                inputRequirements = emptySet()
            ),
            DogInputState(
                weight = "50",
                isWeightValid = true,
                inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
            )
        )
    }

    @Test
    fun `when weight is in kg and less than 225kg, then it is valid`() {
        dogInputState.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }
        dogInputState.updateWeight("100.0")

        dogInputState.value shouldBe DogInputState(
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
            weight = "100.2.22",
            weightFormat = WeightFormat.KILOGRAMS,
            isWeightValid = false,
            inputRequirements = emptySet()
        )
    }

    @Test
    fun `when weight is in kg and is valid then updated to invalid, no input requirements should be met`() {
        val history = createStateHistory()
        dogInputState.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }
        dogInputState.updateWeight("100")
        dogInputState.updateWeight("10000")

        history shouldContainExactly listOf(
            DogInputState(),
            DogInputState(
                weightFormat = WeightFormat.KILOGRAMS
            ),
            DogInputState(
                weight = "100",
                weightFormat = WeightFormat.KILOGRAMS,
                isWeightValid = true,
                inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
            ),
            DogInputState(
                weight = "10000",
                weightFormat = WeightFormat.KILOGRAMS,
                isWeightValid = false,
                inputRequirements = emptySet()
            )
        )
    }

    @Test
    fun `when weight is in kg and is invalid then updated to valid, then input requirements should be met`() {
        val history = createStateHistory()
        dogInputState.update { it.copy(weightFormat = WeightFormat.KILOGRAMS) }
        dogInputState.updateWeight("10000")
        dogInputState.updateWeight("100")

        history shouldContainExactly listOf(
            DogInputState(),
            DogInputState(
                weightFormat = WeightFormat.KILOGRAMS
            ),
            DogInputState(
                weight = "10000",
                weightFormat = WeightFormat.KILOGRAMS,
                isWeightValid = false,
                inputRequirements = emptySet()
            ),
            DogInputState(
                weight = "100",
                weightFormat = WeightFormat.KILOGRAMS,
                isWeightValid = true,
                inputRequirements = setOf(DogInputRequirements.WeightMoreThanZeroAndValidNumberBelow500LbOr225Kg)
            )
        )
    }

    @Test
    fun `when profile pic is set then input requirements are met`() {
        dogInputState.updateProfilePic(Uri.parse("pic"))

        dogInputState.value shouldBe DogInputState(
            profilePic = Uri.parse("pic"),
            inputRequirements = setOf(DogInputRequirements.OnePicture)
        )
    }

    @Test
    fun `when birth date dialog is not shown and birth date is empty, then birth date is valid`() {
        dogInputState.value shouldBe DogInputState(
            birthDateDialogShown = false,
            birthDate = "",
            isBirthDateValid = true
        )
    }

    @Test
    fun `when birth date dialog is shown and birth date is empty, then birthDate is not valid`() {
        dogInputState.updateBirthDateDialogShown()

        dogInputState.value shouldBe DogInputState(
            birthDateDialogShown = true,
            birthDate = "",
            isBirthDateValid = false
        )
    }

    @Test
    fun `when birth date is shown and birth date is not empty, then input requirements are met`() {
        dogInputState.updateBirthDate("12/20/2021")
        dogInputState.updateBirthDateDialogShown()

        dogInputState.value shouldBe DogInputState(
            birthDateDialogShown = true,
            birthDate = "12/20/2021",
            inputRequirements = setOf(DogInputRequirements.BirthDate)
        )
    }

    @Test
    fun `when birth date is not shown and birth date is not empty, then input requirements are met`() {
        dogInputState.updateBirthDate("5/20/2021")

        dogInputState.value shouldBe DogInputState(
            birthDateDialogShown = false,
            birthDate = "5/20/2021",
            inputRequirements = setOf(DogInputRequirements.BirthDate)
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
