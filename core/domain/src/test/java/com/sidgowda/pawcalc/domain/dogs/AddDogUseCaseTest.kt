package com.sidgowda.pawcalc.domain.dogs

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.dogs.model.DogInput
import com.sidgowda.pawcalc.data.dogs.repo.DogsRepo
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AddDogUseCaseTest {

    private lateinit var addDogUseCase: AddDogUseCase
    private lateinit var dogsRepo: DogsRepo
    private lateinit var capturedDogInput: CapturingSlot<DogInput>

    @Before
    fun setup() {
        dogsRepo = mockk()
        capturedDogInput = slot()
        coEvery { dogsRepo.addDog(capture(capturedDogInput)) } just runs
        addDogUseCase = AddDogUseCase(dogsRepo)
    }

    @Test
    fun `verify addDog has not added any dog`() {
        coVerify(exactly = 0) { dogsRepo.addDog(any()) }
    }

    @Test
    fun `verify addDog added the correct dog input`() = runTest {
        addDogUseCase.invoke(DOG_INPUT)
        coVerify(exactly = 1) { dogsRepo.addDog(DOG_INPUT) }
        capturedDogInput.captured shouldBe DOG_INPUT
    }

    @Test
    fun `verify addDog added correct date format`() = runTest {
        addDogUseCase.invoke(DOG_INPUT.copy(birthDate = "20/12/1999", dateFormat = DateFormat.INTERNATIONAL))

        coVerify(exactly = 1) { dogsRepo.addDog(DOG_INPUT.copy(birthDate = "20/12/1999", dateFormat = DateFormat.INTERNATIONAL)) }
        capturedDogInput.captured shouldBe DOG_INPUT.copy(birthDate = "20/12/1999", dateFormat = DateFormat.INTERNATIONAL)
    }

    @Test
    fun `verify addDog added correct weight format`() = runTest {
        addDogUseCase.invoke(DOG_INPUT.copy(weight = "55.0", weightFormat = WeightFormat.KILOGRAMS))

        coVerify(exactly = 1) { dogsRepo.addDog(DOG_INPUT.copy(weight = "55.0", weightFormat = WeightFormat.KILOGRAMS)) }
        capturedDogInput.captured shouldBe DOG_INPUT.copy(weight = "55.0", weightFormat = WeightFormat.KILOGRAMS)
    }

    private companion object {
         val DOG_INPUT = DogInput(
            profilePic = Uri.EMPTY,
            name = "dog",
            weight = "84",
            birthDate = "12/20/1999",
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN
        )
    }
}
