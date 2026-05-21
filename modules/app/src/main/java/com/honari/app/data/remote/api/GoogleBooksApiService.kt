package com.honari.app.data.remote.api

import com.honari.app.data.remote.dto.GoogleBookItem
import com.honari.app.data.remote.dto.GoogleBooksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("orderBy") orderBy: String = "relevance",
        @Query("printType") printType: String = "books",
        @Query("langRestrict") langRestrict: String? = null,
        @Query("key") apiKey: String,
    ): GoogleBooksResponse

    @GET("volumes/{id}")
    suspend fun getBookById(
        @Path("id") id: String,
        @Query("key") apiKey: String,
    ): GoogleBookItem
}
