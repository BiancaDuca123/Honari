package com.honari.app.domain.repository

import com.honari.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun logout()
}
