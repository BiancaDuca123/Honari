package com.honari.app.data.repository

import com.honari.app.BuildConfig
import com.honari.app.data.remote.BooksApiService
import com.honari.app.data.remote.dto.VolumeItem
import com.honari.app.domain.model.Book
import com.honari.app.domain.repository.BookLookupRepository
import javax.inject.Inject

/**
 * Implements [BookLookupRepository] using the Google Books API.
 * ISBN barcodes are used for precise lookups; free-text for manual search.
 */
class BookLookupRepositoryImpl @Inject constructor(private val api: BooksApiService) :
    BookLookupRepository {

    private val apiKey: String get() = BuildConfig.BOOKS_API_KEY

    override suspend fun getBookById(id: String): Result<Book> = runCatching {
        api.getVolumeById(id, apiKey).toDomainModel()
    }

    override suspend fun searchByIsbn(isbn: String): Result<Book> = runCatching {
        val response = api.searchVolumes(query = "isbn:$isbn", maxResults = 1, apiKey = apiKey)
        val item = response.items?.firstOrNull() ?: error("No book found for ISBN: $isbn")
        item.toDomainModel()
    }

    override suspend fun searchByQuery(query: String): Result<List<Book>> = runCatching {
        api.searchVolumes(query = query, apiKey = apiKey)
            .items?.map { it.toDomainModel() } ?: emptyList()
    }

    override suspend fun getPopularBooks(): Result<List<Book>> = runCatching {
        api.searchVolumes(
            query = "subject:fiction",
            maxResults = 20,
            orderBy = "relevance",
            apiKey = apiKey
        ).items?.map { it.toDomainModel() } ?: emptyList()
    }

    override suspend fun getNewReleases(): Result<List<Book>> = runCatching {
        api.searchVolumes(
            query = "subject:fiction",
            maxResults = 20,
            orderBy = "newest",
            apiKey = apiKey
        ).items?.map { it.toDomainModel() } ?: emptyList()
    }

    private fun VolumeItem.toDomainModel(): Book {
        val info = volumeInfo
        val thumbnail = info.imageLinks?.thumbnail
            ?.replace("http://", "https://")
            ?: info.imageLinks?.smallThumbnail?.replace("http://", "https://")
            ?: ""

        val isbn = info.industryIdentifiers
            ?.firstOrNull { it.type == "ISBN_13" }?.identifier
            ?: info.industryIdentifiers?.firstOrNull { it.type == "ISBN_10" }?.identifier
            ?: ""

        return Book(
            id = id,
            title = info.title,
            author = info.authors?.joinToString(", ") ?: "Unknown Author",
            rating = info.averageRating?.toFloat() ?: 0f,
            imageUrl = thumbnail,
            description = info.description ?: "",
            category = info.categories?.firstOrNull(),
            readers = info.ratingsCount ?: 0,
            isbn = isbn,
            pageCount = info.pageCount ?: 0,
            publisher = info.publisher ?: "",
            publishedDate = info.publishedDate ?: ""
        )
    }
}
