package com.honari.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.honari.app.data.local.entity.LibraryBookEntity
import com.honari.app.domain.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM library_books ORDER BY addedAt DESC")
    fun getAllBooks(): Flow<List<LibraryBookEntity>>

    @Query("SELECT * FROM library_books WHERE status = :status ORDER BY addedAt DESC")
    fun getBooksByStatus(status: ReadingStatus): Flow<List<LibraryBookEntity>>

    @Query("SELECT * FROM library_books WHERE bookId = :bookId LIMIT 1")
    suspend fun getBookById(bookId: String): LibraryBookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: LibraryBookEntity)

    @Query("DELETE FROM library_books WHERE bookId = :bookId")
    suspend fun deleteBook(bookId: String)

    @Query("UPDATE library_books SET status = :status WHERE bookId = :bookId")
    suspend fun updateStatus(bookId: String, status: ReadingStatus)

    @Query("UPDATE library_books SET userRating = :rating WHERE bookId = :bookId")
    suspend fun updateRating(bookId: String, rating: Float)

    @Query("SELECT COUNT(*) > 0 FROM library_books WHERE bookId = :bookId")
    fun isBookInLibrary(bookId: String): Flow<Boolean>
}
