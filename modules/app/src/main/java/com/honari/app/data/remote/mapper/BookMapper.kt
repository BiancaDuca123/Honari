package com.honari.app.data.remote.mapper

import com.honari.app.data.remote.dto.GoogleBookItem
import com.honari.app.domain.model.Book

private fun GoogleBookItem.extractIsbn(): String {
    val identifiers = volumeInfo.industryIdentifiers ?: return ""
    return identifiers.firstOrNull { it.type == "ISBN_13" }?.identifier
        ?: identifiers.firstOrNull { it.type == "ISBN_10" }?.identifier
        ?: ""
}

private fun GoogleBookItem.createBook(thumbnail: String, isbn: String): Book = Book(
    id = id,
    title = volumeInfo.title,
    authors = volumeInfo.authors.orEmpty(),
    description = volumeInfo.description ?: "",
    imageUrl = thumbnail,
    isbn = isbn,
    categories = volumeInfo.categories.orEmpty(),
    pageCount = volumeInfo.pageCount ?: 0,
    publishedDate = volumeInfo.publishedDate ?: "",
    averageRating = volumeInfo.averageRating ?: 0f,
    ratingsCount = volumeInfo.ratingsCount ?: 0,
    publisher = volumeInfo.publisher ?: "",
    language = volumeInfo.language ?: "",
)

fun GoogleBookItem.toDomain(): Book {
    val thumbnail = volumeInfo.imageLinks?.thumbnail
        ?.replace("http://", "https://")
        ?: ""
    return createBook(thumbnail = thumbnail, isbn = extractIsbn())
}
