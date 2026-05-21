package com.honari.app.data.local.mapper

import com.honari.app.data.local.entity.LibraryBookEntity
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus

fun LibraryBookEntity.toDomain(): Book = Book(
    id = bookId,
    title = title,
    authors = authors,
    description = description,
    imageUrl = imageUrl,
    isbn = isbn,
    categories = categories,
    pageCount = pageCount,
    publishedDate = publishedDate,
    averageRating = averageRating,
    ratingsCount = ratingsCount,
    publisher = publisher,
    language = language,
    libraryStatus = status,
    addedAt = addedAt,
    userRating = userRating,
)

fun Book.toEntity(): LibraryBookEntity = LibraryBookEntity(
    bookId = id,
    title = title,
    authors = authors,
    description = description,
    imageUrl = imageUrl,
    isbn = isbn,
    categories = categories,
    pageCount = pageCount,
    publishedDate = publishedDate,
    averageRating = averageRating,
    ratingsCount = ratingsCount,
    publisher = publisher,
    language = language,
    status = libraryStatus ?: ReadingStatus.SAVED,
    addedAt = addedAt ?: System.currentTimeMillis(),
    userRating = userRating,
)
