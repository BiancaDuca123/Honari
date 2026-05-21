package com.honari.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val bookId: String,
    val userId: String,
    val displayName: String,
    val photoUrl: String? = null,
    val rating: Float,
    val text: String,
    val createdAt: Long,
)
