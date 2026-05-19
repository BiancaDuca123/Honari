package com.honari.app.data.remote.mapper

import com.honari.app.data.remote.dto.GoogleBookItem
import com.honari.app.domain.model.Book

fun GoogleBookItem.toDomain(): Book {
    val info = volumeInfo
    val thumbnail = info.imageLinks?.thumbnail
        ?.replace("http://", "https://")
        ?: ""
    val isbn = info.industryIdentifiers
        ?.firstOrNull { it.type == "ISBN_13" }?.identifier
        ?: info.industryIdentifiers?.firstOrNull { it.type == "ISBN_10" }?.identifier
        ?: ""

    return Book(
        id = id,
        title = info.title,
        authors = info.authors ?: emptyList(),
        description = info.description ?: "",
        imageUrl = thumbnail,
        isbn = isbn,
        categories = info.categories ?: emptyList(),
        pageCount = info.pageCount ?: 0,
        publishedDate = info.publishedDate ?: "",
        averageRating = info.averageRating ?: 0f,
        ratingsCount = info.ratingsCount ?: 0,
        publisher = info.publisher ?: "",
        language = info.language ?: "",
    )
}
