package com.honari.app.domain.usecase

import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.LibraryRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/** Adds a scanned or discovered book to the user's personal library. */
class AddBookToLibraryUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(
        userId: String,
        book: Book,
        status: ReadingStatus = ReadingStatus.WANT_TO_READ
    ): Result<Unit> {
        val dateAdded = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val bookToSave = book.copy(
            id = book.id.ifBlank { book.isbn.ifBlank { book.title.hashCode().toString() } },
            status = status.name,
            dateAdded = dateAdded
        )
        return libraryRepository.addBook(userId, bookToSave)
    }
}
