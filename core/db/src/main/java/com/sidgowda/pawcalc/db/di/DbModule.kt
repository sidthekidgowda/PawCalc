package com.sidgowda.pawcalc.db.di

import android.content.Context
import androidx.room.Room
import com.sidgowda.pawcalc.db.PawCalcDatabase
import com.sidgowda.pawcalc.db.dog.DogDao
import com.sidgowda.pawcalc.db.settings.SettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {

    @Singleton
    @Provides
    fun providesDatabase(
        @ApplicationContext appContext: Context
    ): PawCalcDatabase {
        return Room.databaseBuilder(
            appContext,
            PawCalcDatabase::class.java,
            "pawcalc-database"
        ).build()
    }

    @Singleton
    @Provides
    fun providesDogDao(
        pawCalcDatabase: PawCalcDatabase
    ): DogDao {
        return pawCalcDatabase.dogDao()
    }

    @Singleton
    @Provides
    fun providesSettingsDao(
        pawCalcDatabase: PawCalcDatabase
    ): SettingsDao {
        return pawCalcDatabase.settingsDao()
    }
}
