package com.sidgowda.pawcalc.data.dogs

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDataSource
import com.sidgowda.pawcalc.data.dogs.datasource.DogsDiskDataSource
import com.sidgowda.pawcalc.db.dog.DogEntity
import com.sidgowda.pawcalc.db.dog.DogsDao
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DogsDiskDataSourceTest {

    private lateinit var dogsDataSource: DogsDataSource
    private lateinit var dogsDao: DogsDao

    @Before
    fun setup() {
        dogsDao = mockk()
        dogsDataSource = DogsDiskDataSource(dogsDao)
    }

    @Test
    fun `assertDiskDataSourcIsEmpty`() = runTest {
        coEvery { dogsDao.dogs() } returns flowOf(emptyList())

        dogsDataSource.dogs().test {
            assertEquals(emptyList<DogEntity>(), awaitItem())
            awaitComplete()
        }
    }
}
