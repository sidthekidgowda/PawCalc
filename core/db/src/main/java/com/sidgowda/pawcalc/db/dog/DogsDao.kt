package com.sidgowda.pawcalc.db.dog

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DogsDao {
    @Query("SELECT * FROM dogs")
    fun dogs(): Flow<List<DogEntity>>

    @Query("SELECT * FROM dogs WHERE id = :id")
    suspend fun dogForId(id: Int): DogEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDog(dog: DogEntity)

    @Delete
    suspend fun deleteDog(dog: DogEntity)

    @Update
    suspend fun updateDog(dog: DogEntity)
}
