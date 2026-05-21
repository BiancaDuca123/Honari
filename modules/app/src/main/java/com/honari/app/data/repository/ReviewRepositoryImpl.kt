package com.honari.app.data.repository

import com.honari.app.data.local.dao.ReviewDao
import com.honari.app.data.local.entity.ReviewEntity
import com.honari.app.domain.model.Review
import com.honari.app.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
) : ReviewRepository {

    override fun getReviewsForBook(bookId: String): Flow<List<Review>> =
        reviewDao.getReviewsByBookId(bookId).map { list -> list.map { it.toDomain() } }

    override suspend fun addReview(review: Review): Result<Unit> = runCatching {
        val entity = review.toEntity()
        reviewDao.insertReview(entity)
    }

    override suspend fun deleteReview(reviewId: String): Result<Unit> = runCatching {
        reviewDao.deleteReview(reviewId)
    }

    private fun ReviewEntity.toDomain() = Review(
        id = id,
        bookId = bookId,
        userId = userId,
        displayName = displayName,
        photoUrl = photoUrl,
        rating = rating,
        text = text,
        createdAt = createdAt,
    )

    private fun Review.toEntity() = ReviewEntity(
        id = id.ifEmpty { UUID.randomUUID().toString() },
        bookId = bookId,
        userId = userId,
        displayName = displayName,
        photoUrl = photoUrl,
        rating = rating,
        text = text,
        createdAt = createdAt,
    )
}
