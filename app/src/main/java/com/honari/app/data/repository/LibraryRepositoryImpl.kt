package com.honari.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.LibraryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firestore-backed implementation of [LibraryRepository].
 * Books live at: users/{userId}/library/{bookId}
 */
class LibraryRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    LibraryRepository {

    override fun getUserLibrary(userId: String): Flow<List<Book>> = callbackFlow {
        val listener = firestore
            .collection("users").document(userId)
            .collection("library")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val books = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Book::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(books)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addBook(userId: String, book: Book): Result<Unit> = runCatching {
        firestore
            .collection("users").document(userId)
            .collection("library").document(book.id)
            .set(book)
            .await()
    }

    override suspend fun updateStatus(
        userId: String,
        bookId: String,
        status: ReadingStatus
    ): Result<Unit> = runCatching {
        firestore
            .collection("users").document(userId)
            .collection("library").document(bookId)
            .update("status", status.name)
            .await()
    }

    override suspend fun updateProgress(
        userId: String,
        bookId: String,
        progress: Int
    ): Result<Unit> = runCatching {
        firestore
            .collection("users").document(userId)
            .collection("library").document(bookId)
            .update("progress", progress)
            .await()
    }

    override suspend fun removeBook(userId: String, bookId: String): Result<Unit> = runCatching {
        firestore
            .collection("users").document(userId)
            .collection("library").document(bookId)
            .delete()
            .await()
    }
}
