package com.honari.app.domain.repository

import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getAllBooks(): Flow<List<Book>>
    fun getBooksByStatus(status: ReadingStatus): Flow<List<Book>>
    suspend fun getBookById(bookId: String): Book?
    suspend fun addBook(book: Book)
    suspend fun removeBook(bookId: String)
    suspend fun updateStatus(bookId: String, status: ReadingStatus)
    suspend fun updateRating(bookId: String, rating: Float)
    fun isBookInLibrary(bookId: String): Flow<Boolean>
}
