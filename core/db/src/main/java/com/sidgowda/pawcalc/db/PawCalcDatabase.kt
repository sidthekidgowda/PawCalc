package com.sidgowda.pawcalc.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sidgowda.pawcalc.db.dog.Dog
import com.sidgowda.pawcalc.db.dog.DogDao
import com.sidgowda.pawcalc.db.dog.DogTypeConverter
import com.sidgowda.pawcalc.db.settings.Settings
import com.sidgowda.pawcalc.db.settings.SettingsDao

@Database(
    entities = [Dog::class, Settings::class],
    version = 1
)
@TypeConverters(DogTypeConverter::class)
abstract class PawCalcDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao
    abstract fun settingsDao(): SettingsDao
}
