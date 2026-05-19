package com.honari.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.honari.app.domain.model.Review
import com.honari.app.domain.repository.ReviewRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ReviewRepository {

    private val reviewsCollection = firestore.collection("reviews")

    override fun getReviewsForBook(bookId: String): Flow<List<Review>> = callbackFlow {
        val listener = reviewsCollection
            .whereEqualTo("bookId", bookId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                val reviews = if (error != null) {
                    emptyList()
                } else {
                    snapshot?.documents?.mapNotNull { document ->
                        document.toObject(Review::class.java)?.copy(id = document.id)
                    } ?: emptyList()
                }
                trySend(reviews)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addReview(review: Review): Result<Unit> =
        runCatching { reviewsCollection.add(review).await() }

    override suspend fun deleteReview(reviewId: String): Result<Unit> =
        runCatching { reviewsCollection.document(reviewId).delete().await() }
}
