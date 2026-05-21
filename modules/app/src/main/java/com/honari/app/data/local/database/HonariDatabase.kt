package com.honari.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.honari.app.data.local.converter.Converters
import com.honari.app.data.local.dao.LibraryDao
import com.honari.app.data.local.dao.ReviewDao
import com.honari.app.data.local.entity.LibraryBookEntity
import com.honari.app.data.local.entity.ReviewEntity

@Database(entities = [LibraryBookEntity::class, ReviewEntity::class], version = 2, exportSchema = true)
@TypeConverters(Converters::class)
abstract class HonariDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao
    abstract fun reviewDao(): ReviewDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE library_books ADD COLUMN description TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE library_books ADD COLUMN categories TEXT NOT NULL DEFAULT '[]'")
        database.execSQL("ALTER TABLE library_books ADD COLUMN pageCount INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE library_books ADD COLUMN publishedDate TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE library_books ADD COLUMN averageRating REAL NOT NULL DEFAULT 0.0")
        database.execSQL("ALTER TABLE library_books ADD COLUMN ratingsCount INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE library_books ADD COLUMN publisher TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE library_books ADD COLUMN language TEXT NOT NULL DEFAULT ''")
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS reviews (
                id TEXT NOT NULL PRIMARY KEY,
                bookId TEXT NOT NULL,
                userId TEXT NOT NULL,
                displayName TEXT NOT NULL,
                photoUrl TEXT,
                rating REAL NOT NULL,
                text TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }
}
