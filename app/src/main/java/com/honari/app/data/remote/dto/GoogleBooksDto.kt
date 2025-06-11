package com.honari.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleBooksResponse(
    @SerializedName("totalItems") val totalItems: Int = 0,
    @SerializedName("items") val items: List<VolumeItem>? = null
)

data class VolumeItem(
    @SerializedName("id") val id: String = "",
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfo = VolumeInfo()
)

data class VolumeInfo(
    @SerializedName("title") val title: String = "",
    @SerializedName("authors") val authors: List<String>? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("imageLinks") val imageLinks: ImageLinks? = null,
    @SerializedName("averageRating") val averageRating: Double? = null,
    @SerializedName("ratingsCount") val ratingsCount: Int? = null,
    @SerializedName("categories") val categories: List<String>? = null,
    @SerializedName("pageCount") val pageCount: Int? = null,
    @SerializedName("publisher") val publisher: String? = null,
    @SerializedName("publishedDate") val publishedDate: String? = null,
    @SerializedName("industryIdentifiers") val industryIdentifiers: List<IndustryIdentifier>? = null
)

data class ImageLinks(
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("smallThumbnail") val smallThumbnail: String? = null
)

data class IndustryIdentifier(
    @SerializedName("type") val type: String = "",
    @SerializedName("identifier") val identifier: String = ""
)
