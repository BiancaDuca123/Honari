package com.honari.app.data.local.converter

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(listType)

    @TypeConverter
    fun fromStringList(value: List<String>): String = adapter.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = adapter.fromJson(value) ?: emptyList()
}
