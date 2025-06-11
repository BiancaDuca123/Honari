package com.honari.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.honari.app.domain.model.User
import com.honari.app.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementation of AuthRepository using Firebase Auth and Firestore.
 */
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                trySend(
                    User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "",
                        profileImageUrl = firebaseUser.photoUrl?.toString()
                    )
                )
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun isLoggedIn(): Boolean = auth.currentUser != null

    override suspend fun signInWithGoogle(idToken: String): Result<User> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()
        val firebaseUser = authResult.user ?: throw Exception("Google sign-in returned no user")

        val user = User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: ""
        )

        // Best-effort Firestore write — don't fail auth if this fails
        try {
            firestore.collection("users").document(user.id).set(user).await()
        } catch (ignored: Exception) {
            // User is authenticated — Firestore profile write is non-critical
        }

        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun login(email: String, password: String): Result<User> = try {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Login failed")

        val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
        val user = userDoc.toObject(User::class.java)
            ?: User(id = firebaseUser.uid, email = firebaseUser.email ?: "", displayName = "")

        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<User> = try {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Registration failed")

        val user = User(id = firebaseUser.uid, email = email, displayName = displayName)
        firestore.collection("users").document(firebaseUser.uid).set(user).await()

        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logout() = auth.signOut()

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateProfile(user: User): Result<User> = try {
        firestore.collection("users").document(user.id).set(user).await()
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
