package com.sidgowda.pawcalc.db.dog

import androidx.room.*

@Dao
interface DogDao {

    @Query("SELECT * FROM dogs")
    fun dogs(): List<Dog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDog(dog: Dog)

    @Delete
    fun deleteDog(dog: Dog)

    @Update
    fun updateDog(dog: Dog)
}
