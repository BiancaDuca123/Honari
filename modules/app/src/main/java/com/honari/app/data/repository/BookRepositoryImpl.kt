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

private const val MIN_YEAR = 1995
private const val POPULAR_THRESHOLD = 1000
private const val FETCH_MULTIPLIER = 2
private const val API_MAX_RESULTS = 40

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
            maxResults = (maxResults * FETCH_MULTIPLIER).coerceAtMost(API_MAX_RESULTS),
            orderBy = "newest",
            apiKey = apiKey,
        ).items
            ?.map { it.toDomain() }
            ?.distinctBy { it.id }
            ?.filter { it.isEligibleForFeed() }
            ?.take(maxResults)
            ?: emptyList()
        emit(books)
    }

    override suspend fun searchBooks(query: String, maxResults: Int): List<Book> =
        api.searchBooks(
            query = query,
            maxResults = (maxResults * FETCH_MULTIPLIER).coerceAtMost(API_MAX_RESULTS),
            apiKey = apiKey,
        )
            .items
            ?.map { it.toDomain() }
            ?.distinctBy { it.id }
            ?.filter { it.isEligibleForFeed() }
            ?.take(maxResults)
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

private fun Book.isEligibleForFeed(): Boolean {
    if (imageUrl.isEmpty()) return false
    val year = publishedDate.take(4).toIntOrNull() ?: 0
    return year >= MIN_YEAR || ratingsCount >= POPULAR_THRESHOLD
}
