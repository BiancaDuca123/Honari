package com.honari.app.data.repository

import com.honari.app.BuildConfig
import com.honari.app.data.remote.api.GoogleBooksApiService
import com.honari.app.data.remote.mapper.toDomain
import com.honari.app.domain.model.Book
import com.honari.app.domain.repository.BookRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

private const val API_MAX_RESULTS = 40
private const val FEED_RESULTS_PER_QUERY = 20
private const val LANG_RO = "ro"

// Queries that surface books actually popular on Romanian market
private val POPULAR_RO_QUERIES = listOf(
    "bestseller fiction",
    "bestseller thriller",
    "bestseller romance",
    "popular fantasy",
    "bestseller mystery",
)

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val api: GoogleBooksApiService,
) : BookRepository {

    private val apiKey get() = BuildConfig.GOOGLE_BOOKS_API_KEY

    override fun getFeedBooks(
        query: String,
        maxResults: Int,
    ): Flow<List<Book>> = flow {
        emit(fetchPopularRoBooks(maxResults))
    }

    private suspend fun fetchPopularRoBooks(maxResults: Int): List<Book> = coroutineScope {
        // Fetch multiple popular queries in parallel, restricted to Romanian language
        val deferred = POPULAR_RO_QUERIES.map { q ->
            async {
                runCatching {
                    api.searchBooks(
                        query = q,
                        maxResults = FEED_RESULTS_PER_QUERY,
                        orderBy = "relevance",
                        langRestrict = LANG_RO,
                        apiKey = apiKey,
                    ).items?.map { it.toDomain() } ?: emptyList()
                }.getOrDefault(emptyList())
            }
        }
        deferred.awaitAll()
            .flatten()
            .filter { it.imageUrl.isNotEmpty() }
            .distinctBy { it.id }
            .sortedByDescending { it.ratingsCount }
            .take(maxResults)
    }

    override suspend fun searchBooks(query: String, maxResults: Int): List<Book> =
        api.searchBooks(
            query = query,
            maxResults = maxResults.coerceAtMost(API_MAX_RESULTS),
            apiKey = apiKey,
        )
            .items
            ?.map { it.toDomain() }
            ?.distinctBy { it.id }
            ?.filter { it.imageUrl.isNotEmpty() }
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
