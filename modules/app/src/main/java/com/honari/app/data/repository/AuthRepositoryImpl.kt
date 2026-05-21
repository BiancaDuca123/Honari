package com.honari.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.honari.app.domain.model.User
import com.honari.app.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.toUser()) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> =
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val fbUser = requireNotNull(auth.signInWithCredential(credential).await().user) {
                "Firebase user is null after Google sign-in"
            }
            fbUser.toUser()
        }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> =
        runCatching {
            val fbUser = requireNotNull(
                auth.signInWithEmailAndPassword(email, password).await().user,
            ) { "Firebase user is null after email sign-in" }
            fbUser.toUser()
        }

    override suspend fun registerWithEmail(
        email: String,
        displayName: String,
        password: String,
    ): Result<User> = runCatching {
        val fbUser = requireNotNull(
            auth.createUserWithEmailAndPassword(email, password).await().user,
        ) { "Firebase user is null after registration" }
        val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        fbUser.updateProfile(profileUpdate).await()
        fbUser.reload().await()
        (auth.currentUser ?: fbUser).toUser()
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        runCatching { auth.sendPasswordResetEmail(email).await() }

    override suspend fun logout() = auth.signOut()
}

private fun FirebaseUser.toUser() = User(
    id = uid,
    email = email ?: "",
    displayName = displayName ?: "",
    photoUrl = photoUrl?.toString(),
)
