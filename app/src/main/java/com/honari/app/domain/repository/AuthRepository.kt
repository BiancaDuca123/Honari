package com.honari.app.domain.repository

import com.honari.app.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, displayName: String): Result<User>
    suspend fun logout()
    fun getCurrentUser(): Flow<User?>
    suspend fun updateProfile(user: User): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signInWithGoogle(idToken: String): Result<User>
}