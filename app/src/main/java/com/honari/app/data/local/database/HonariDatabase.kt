package com.honari.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.honari.app.data.local.converter.Converters
import com.honari.app.data.local.dao.LibraryDao
import com.honari.app.data.local.entity.LibraryBookEntity

@Database(entities = [LibraryBookEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class HonariDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao
}
