package com.honari.app.domain.repository

import com.honari.app.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getFeedBooks(query: String = "subject:fiction", maxResults: Int = 20): Flow<List<Book>>
    suspend fun searchBooks(query: String, maxResults: Int = 20): List<Book>
    suspend fun getBookById(googleBooksId: String): Book?
    suspend fun getBookByIsbn(isbn: String): Book?
}
