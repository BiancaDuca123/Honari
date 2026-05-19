package com.honari.app.data.repository

import com.honari.app.data.local.dao.LibraryDao
import com.honari.app.data.local.mapper.toDomain
import com.honari.app.data.local.mapper.toEntity
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val dao: LibraryDao,
) : LibraryRepository {

    override fun getAllBooks(): Flow<List<Book>> =
        dao.getAllBooks().map { list -> list.map { it.toDomain() } }

    override fun getBooksByStatus(status: ReadingStatus): Flow<List<Book>> =
        dao.getBooksByStatus(status).map { list -> list.map { it.toDomain() } }

    override suspend fun getBookById(bookId: String): Book? =
        dao.getBookById(bookId)?.toDomain()

    override suspend fun addBook(book: Book) =
        dao.insertBook(book.toEntity())

    override suspend fun removeBook(bookId: String) =
        dao.deleteBook(bookId)

    override suspend fun updateStatus(bookId: String, status: ReadingStatus) =
        dao.updateStatus(bookId, status)

    override suspend fun updateRating(bookId: String, rating: Float) =
        dao.updateRating(bookId, rating)

    override fun isBookInLibrary(bookId: String): Flow<Boolean> =
        dao.isBookInLibrary(bookId)
}
