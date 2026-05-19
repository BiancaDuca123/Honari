package com.honari.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.honari.app.data.local.converter.Converters
import com.honari.app.domain.model.ReadingStatus

@Entity(tableName = "library_books")
@TypeConverters(Converters::class)
data class LibraryBookEntity(
    @PrimaryKey val bookId: String,
    val title: String,
    val authors: List<String>,
    val imageUrl: String,
    val isbn: String,
    val status: ReadingStatus,
    val addedAt: Long,
    val userRating: Float,
)
