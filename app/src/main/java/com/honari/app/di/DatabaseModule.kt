package com.honari.app.di

import android.content.Context
import androidx.room.Room
import com.honari.app.data.local.dao.LibraryDao
import com.honari.app.data.local.database.HonariDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideHonariDatabase(@ApplicationContext context: Context): HonariDatabase =
        Room.databaseBuilder(context, HonariDatabase::class.java, "honari.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideLibraryDao(database: HonariDatabase): LibraryDao = database.libraryDao()
}
