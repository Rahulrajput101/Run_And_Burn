package com.androiddevs.runandburn.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.media.MediaCodec.MetricsConstants.MODE
import android.view.Display.Mode
import android.content.SharedPreferences
import android.widget.AdapterView
import androidx.room.Room
import com.androiddevs.runandburn.db.RunningDatabase
import com.androiddevs.runandburn.utlis.Constants.KEY_FIRST_TIME
import com.androiddevs.runandburn.utlis.Constants.KEY_NAME
import com.androiddevs.runandburn.utlis.Constants.KEY_ONLY_ONE_TIME
import com.androiddevs.runandburn.utlis.Constants.KEY_WEIGHT
import com.androiddevs.runandburn.utlis.Constants.RUNNING_DATABASE_NAME
import com.androiddevs.runandburn.utlis.Constants.SHARED_PREFERENCE_NAME
import dagger.Binds
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

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app : Context) =
        app.getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences)  = sharedPreferences.getString(KEY_NAME,"") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences)  = sharedPreferences.getFloat(KEY_WEIGHT, 60f)

    @Singleton
    @Provides
    fun provideFirst(sharedPreferences: SharedPreferences)  = sharedPreferences.getBoolean(
        KEY_FIRST_TIME, true)






}