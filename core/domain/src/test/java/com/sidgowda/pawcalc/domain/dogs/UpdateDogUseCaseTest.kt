package com.sidgowda.pawcalc.domain.dogs

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class UpdateDogUseCaseTest {

    private lateinit var updateDogUseCase: UpdateDogUseCase
    private lateinit var dogsRepo: DogsRepo
    private lateinit var capturedDog: CapturingSlot<Dog>

    @Before
    fun setup() {
        dogsRepo = mockk()
        capturedDog = slot()
        coEvery { dogsRepo.updateDog(capture(capturedDog)) } just runs
        updateDogUseCase = UpdateDogUseCase(dogsRepo)
    }

    @Test
    fun `verify updateDog has not updated any dog`() {
        coVerify(exactly = 0) { dogsRepo.updateDog(any()) }
    }

    @Test
    fun `verify updateDog updated the correct dog`() = runTest() {
        updateDogUseCase.invoke(DOG)
        coVerify(exactly = 1) { dogsRepo.updateDog(DOG) }
        capturedDog.captured shouldBe DOG
    }

    @Test
    fun `verify updateDog updated with correct weight format`() = runTest {
        updateDogUseCase.invoke(DOG.copy(weightFormat = WeightFormat.KILOGRAMS))
        coVerify(exactly = 1) { dogsRepo.updateDog(DOG.copy(weightFormat = WeightFormat.KILOGRAMS)) }
        capturedDog.captured shouldBe DOG.copy(weightFormat = WeightFormat.KILOGRAMS)
    }

    @Test
    fun `verify updateDog updated with correct date format`() = runTest {
        updateDogUseCase.invoke(DOG.copy(birthDate = "20/5/2000", dateFormat = DateFormat.INTERNATIONAL))
        coVerify(exactly = 1) { dogsRepo.updateDog(DOG.copy(birthDate = "20/5/2000", dateFormat = DateFormat.INTERNATIONAL)) }
        capturedDog.captured shouldBe DOG.copy(birthDate = "20/5/2000", dateFormat = DateFormat.INTERNATIONAL)
    }

    private companion object {
        val DOG = Dog(
            id = 1,
            profilePic = Uri.EMPTY,
            name = "dog",
            weight = 84.0,
            birthDate = "12/20/1999",
            dogYears =  "12/20/1999".toDogYears(),
            humanYears = "12/20/1999".toHumanYears(),
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
    }
}
