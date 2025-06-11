package com.honari.app.domain.repository

import com.honari.app.domain.model.Book
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for book data operations.
 * Follows Interface Segregation Principle.
 */
interface BookRepository {
    fun getFeaturedBooks(): Flow<List<Book>>
    fun getTrendingBooks(): Flow<List<Book>>
    fun getBooksByMood(mood: String): Flow<List<Book>>
    suspend fun getBookById(bookId: String): Book?
    fun searchBooks(query: String): Flow<List<Book>>
}