package com.honari.app.domain.model

data class Review(
    val id: String = "",
    val bookId: String = "",
    val userId: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val rating: Float = 0f,
    val text: String = "",
    val createdAt: Long = 0L,
)
