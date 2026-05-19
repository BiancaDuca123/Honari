package com.honari.app.data.local.mapper

import com.honari.app.data.local.entity.LibraryBookEntity
import com.honari.app.domain.model.Book

fun LibraryBookEntity.toDomain(): Book = Book(
    id = bookId,
    title = title,
    authors = authors,
    imageUrl = imageUrl,
    isbn = isbn,
    libraryStatus = status,
    addedAt = addedAt,
    userRating = userRating,
)

fun Book.toEntity(): LibraryBookEntity = LibraryBookEntity(
    bookId = id,
    title = title,
    authors = authors,
    imageUrl = imageUrl,
    isbn = isbn,
    status = requireNotNull(libraryStatus) { "Cannot persist a Book without a libraryStatus" },
    addedAt = addedAt ?: System.currentTimeMillis(),
    userRating = userRating,
)
