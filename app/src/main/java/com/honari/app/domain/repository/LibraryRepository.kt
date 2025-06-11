package com.honari.app.domain.repository

import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for the user's personal book library.
 * Books are persisted per user in Firestore.
 */
interface LibraryRepository {
    fun getUserLibrary(userId: String): Flow<List<Book>>
    suspend fun addBook(userId: String, book: Book): Result<Unit>
    suspend fun updateStatus(userId: String, bookId: String, status: ReadingStatus): Result<Unit>
    suspend fun updateProgress(userId: String, bookId: String, progress: Int): Result<Unit>
    suspend fun removeBook(userId: String, bookId: String): Result<Unit>
}
