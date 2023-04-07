package com.sidgowda.pawcalc.newdog

import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputRequirements
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.newdog.ui.NewDogViewModel
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewDogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var scope: TestScope
    private lateinit var viewModel: NewDogViewModel

    @Before
    fun setup() {
        viewModel = NewDogViewModel()
        scope = TestScope()
    }

    @Test
    fun `given viewModel is initialized, it should have an initial state`() = scope.runTest {
        val history = viewModel.createStateHistory()

        history shouldContainExactly listOf(
            INITIAL_STATE
        )
    }

    @Test
    fun `when name is updated, property name and input requirements should be updated`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.NameChanged("Hello"))

        advanceUntilIdle()
        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                name = "Hello",
                inputRequirements = setOf(DogInputRequirements.NAME_BETWEEN_ONE_AND_FIFTY)
            )
        )
    }

    // check name is more than 0 and less than 50
    // check error state of name when no input

    @Test
    fun `when weight is updated, property weight and input requirements should be updated`() = scope.runTest {
        val history = viewModel.createStateHistory()
        viewModel.handleEvent(DogInputEvent.WeightChanged("50"))

        advanceUntilIdle()
        history shouldContainExactly listOf(
            INITIAL_STATE,
            DogInputState(
                weight = "50",
                inputRequirements = setOf(DogInputRequirements.WEIGHT_MORE_THAN_ZERO_AND_VALID_NUMBER)
            )
        )
    }

    // check weight is a number and more than zero
    // check weight error state

    // test profile pic
    // test name
    // test weight
    // test birth date
    // test save button
    // test name requirements, error
    // test weight requirements, error
    // test birth date requirements, error
    // test valid to invalid state
    // test invalid to valid state

    private fun NewDogViewModel.createStateHistory(): List<DogInputState> {
        val history = mutableListOf<DogInputState>()
        scope.backgroundScope.launch(UnconfinedTestDispatcher()) {
            inputState.toCollection(history)
        }
        return history
    }

    private companion object {
        private val INITIAL_STATE = DogInputState()
    }
}

