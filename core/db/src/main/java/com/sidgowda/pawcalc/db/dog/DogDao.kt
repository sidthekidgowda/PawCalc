package com.sidgowda.pawcalc.db.dog

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DogDao {
    @Query("SELECT * FROM dogs")
    fun dogs(): Flow<List<Dog>>

    @Query("SELECT * FROM dogs WHERE id = :id")
    suspend fun dogForId(id: Int): Dog

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDog(dog: Dog)

    @Delete
    suspend fun deleteDog(dog: Dog)

    @Update
    suspend fun updateDog(dog: Dog)
}
