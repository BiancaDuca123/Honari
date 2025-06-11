package com.honari.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.honari.app.data.repository.AuthRepositoryImpl
import com.honari.app.data.repository.BookRepositoryImpl
import com.honari.app.domain.repository.AuthRepository
import com.honari.app.domain.repository.BookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Main application module for dependency injection.
 * Provides singleton instances for the entire application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides Firebase Auth instance.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Provides Firebase Firestore instance.
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Provides AuthRepository implementation.
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(auth, firestore)

    /**
     * Provides BookRepository implementation.
     */
    @Provides
    @Singleton
    fun provideBookRepository(
        firestore: FirebaseFirestore
    ): BookRepository = BookRepositoryImpl(firestore)
}

/**
 * Extension property for DataStore.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "honari_preferences")

/**
 * Module for DataStore related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * Provides DataStore instance.
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}