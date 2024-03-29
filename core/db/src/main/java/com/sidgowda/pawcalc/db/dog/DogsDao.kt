package com.sidgowda.pawcalc.db.dog

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DogsDao {
    @Query("SELECT * FROM dogs")
    fun dogs(): Flow<List<DogEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDog(vararg dog: DogEntity)

    @Delete
    suspend fun deleteDog(dog: DogEntity)

    @Update
    suspend fun updateDog(vararg dog: DogEntity)

    @Query("DELETE FROM dogs")
    suspend fun deleteAll()
}
