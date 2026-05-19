package com.honari.app.di

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
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindBookRepository(impl: BookRepositoryImpl): BookRepository

    @Binds @Singleton
    abstract fun bindLibraryRepository(impl: LibraryRepositoryImpl): LibraryRepository

    @Binds @Singleton
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository
}
