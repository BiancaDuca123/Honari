package com.honari.app.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.honari.app.data.local.dao.LibraryDao
import com.honari.app.data.local.database.HonariDatabase
import com.honari.app.data.repository.AuthRepositoryImpl
import com.honari.app.data.repository.BookRepositoryImpl
import com.honari.app.data.repository.LibraryRepositoryImpl
import com.honari.app.data.repository.ReviewRepositoryImpl
import com.honari.app.domain.repository.AuthRepository
import com.honari.app.domain.repository.BookRepository
import com.honari.app.domain.repository.LibraryRepository
import com.honari.app.domain.repository.ReviewRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindBookRepository(impl: BookRepositoryImpl): BookRepository

    @Binds @Singleton
    abstract fun bindLibraryRepository(impl: LibraryRepositoryImpl): LibraryRepository

    @Binds @Singleton
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    companion object {

        @Provides @Singleton
        fun provideDatabase(@ApplicationContext context: Context): HonariDatabase =
            Room.databaseBuilder(context, HonariDatabase::class.java, "honari.db")
                .fallbackToDestructiveMigration()
                .build()

        @Provides @Singleton
        fun provideLibraryDao(db: HonariDatabase): LibraryDao = db.libraryDao()

        @Provides @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides @Singleton
        fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    }
}
