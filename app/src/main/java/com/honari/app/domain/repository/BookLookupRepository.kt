package com.honari.app.domain.repository

import com.honari.app.domain.model.Book

/**
 * Repository interface for external book lookup (Google Books API).
 * Used by both the scanner and the search feature.
 */
interface BookLookupRepository {
    /** Look up a book by its ISBN barcode. */
    suspend fun searchByIsbn(isbn: String): Result<Book>

    /** Search books by a free-text query (title, author, etc.). */
    suspend fun searchByQuery(query: String): Result<List<Book>>

    /** Get book details by book ID. */
    suspend fun getBookById(id: String): Result<Book>

    /** Get a list of popular books. */
    suspend fun getPopularBooks(): Result<List<Book>>

    /** Get a list of new book releases. */
    suspend fun getNewReleases(): Result<List<Book>>
}
