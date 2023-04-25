package com.sidgowda.pawcalc.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sidgowda.pawcalc.db.dog.DogsDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DogsDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: PawCalcDatabase
    private lateinit var dogsDao: DogsDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PawCalcDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        dogsDao = database.dogDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun testDatabaseIsEmpty() = runTest {
        dogsDao.dogs().test {

        }
    }
}
