package com.honari.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.honari.app.domain.model.Book
import com.honari.app.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementation of BookRepository using Firebase Firestore.
 * Follows Dependency Inversion Principle.
 */
class BookRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BookRepository {

    override fun getFeaturedBooks(): Flow<List<Book>> = flow {
        try {
            val snapshot = firestore.collection("books")
                .whereEqualTo("featured", true)
                .get()
                .await()

            val books = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Book::class.java)?.copy(id = doc.id)
            }
            emit(books)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getTrendingBooks(): Flow<List<Book>> = flow {
        try {
            val snapshot = firestore.collection("books")
                .whereNotEqualTo("trend", null)
                .get()
                .await()

            val books = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Book::class.java)?.copy(id = doc.id)
            }
            emit(books)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getBooksByMood(mood: String): Flow<List<Book>> = flow {
        try {
            val snapshot = firestore.collection("books")
                .whereEqualTo("mood", mood)
                .get()
                .await()

            val books = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Book::class.java)?.copy(id = doc.id)
            }
            emit(books)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getBookById(bookId: String): Book? {
        return try {
            val doc = firestore.collection("books")
                .document(bookId)
                .get()
                .await()

            doc.toObject(Book::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    override fun searchBooks(query: String): Flow<List<Book>> = flow {
        // Implement search logic
        emit(emptyList())
    }
}