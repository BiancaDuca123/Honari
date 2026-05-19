package com.honari.app.data.repository

import com.honari.app.BuildConfig
import com.honari.app.data.remote.api.GoogleBooksApiService
import com.honari.app.data.remote.mapper.toDomain
import com.honari.app.domain.model.Book
import com.honari.app.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val api: GoogleBooksApiService,
) : BookRepository {

    private val apiKey get() = BuildConfig.GOOGLE_BOOKS_API_KEY

    override fun getFeedBooks(
        query: String,
        maxResults: Int,
    ): Flow<List<Book>> = flow {
        val books = api.searchBooks(
            query = query,
            maxResults = maxResults,
            orderBy = "newest",
            apiKey = apiKey,
        ).items?.map { it.toDomain() } ?: emptyList()
        emit(books)
    }

    override suspend fun searchBooks(query: String, maxResults: Int): List<Book> =
        api.searchBooks(query = query, maxResults = maxResults, apiKey = apiKey)
            .items
            ?.map { it.toDomain() }
            ?: emptyList()

    override suspend fun getBookById(googleBooksId: String): Book? =
        runCatching { api.getBookById(googleBooksId, apiKey).toDomain() }.getOrNull()

    override suspend fun getBookByIsbn(isbn: String): Book? =
        runCatching {
            api.searchBooks(query = "isbn:$isbn", maxResults = 1, apiKey = apiKey)
                .items
                ?.firstOrNull()
                ?.toDomain()
        }.getOrNull()
}
