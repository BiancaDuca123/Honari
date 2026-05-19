package com.honari.app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleBooksResponse(
    @Json(name = "items") val items: List<GoogleBookItem>? = null,
    @Json(name = "totalItems") val totalItems: Int = 0,
)

@JsonClass(generateAdapter = true)
data class GoogleBookItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "volumeInfo") val volumeInfo: VolumeInfo = VolumeInfo(),
)

@JsonClass(generateAdapter = true)
data class VolumeInfo(
    @Json(name = "title") val title: String = "",
    @Json(name = "authors") val authors: List<String>? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "imageLinks") val imageLinks: ImageLinks? = null,
    @Json(name = "industryIdentifiers") val industryIdentifiers: List<IndustryIdentifier>? = null,
    @Json(name = "categories") val categories: List<String>? = null,
    @Json(name = "pageCount") val pageCount: Int? = null,
    @Json(name = "publishedDate") val publishedDate: String? = null,
    @Json(name = "averageRating") val averageRating: Float? = null,
    @Json(name = "ratingsCount") val ratingsCount: Int? = null,
    @Json(name = "publisher") val publisher: String? = null,
    @Json(name = "language") val language: String? = null,
)

@JsonClass(generateAdapter = true)
data class ImageLinks(
    @Json(name = "thumbnail") val thumbnail: String? = null,
    @Json(name = "smallThumbnail") val smallThumbnail: String? = null,
)

@JsonClass(generateAdapter = true)
data class IndustryIdentifier(
    @Json(name = "type") val type: String = "",
    @Json(name = "identifier") val identifier: String = "",
)
