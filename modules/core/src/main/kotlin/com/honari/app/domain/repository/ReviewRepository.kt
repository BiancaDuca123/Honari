package com.honari.app.domain.repository

import com.honari.app.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getReviewsForBook(bookId: String): Flow<List<Review>>
    suspend fun addReview(review: Review): Result<Unit>
    suspend fun deleteReview(reviewId: String): Result<Unit>
}
