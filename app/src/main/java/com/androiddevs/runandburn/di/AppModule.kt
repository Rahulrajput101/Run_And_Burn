package com.androiddevs.runandburn.di

import android.content.Context
import androidx.room.Room
import com.androiddevs.runandburn.db.RunningDatabase
import com.androiddevs.runandburn.utlis.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun runnningDatabase(
        @ApplicationContext app : Context
    ) = Room.databaseBuilder(
      app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun getRunDao(db : RunningDatabase) = db.getRunDao()
}