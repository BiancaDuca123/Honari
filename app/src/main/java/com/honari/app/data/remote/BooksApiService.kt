package com.honari.app.data.remote

import com.honari.app.data.remote.dto.GoogleBooksResponse
import com.honari.app.data.remote.dto.VolumeItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the Google Books REST API.
 * Base URL: https://www.googleapis.com/books/v1/
 */
interface BooksApiService {

    @GET("volumes")
    suspend fun searchVolumes(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("orderBy") orderBy: String = "relevance",
        @Query("key") apiKey: String = ""
    ): GoogleBooksResponse

    @GET("volumes/{id}")
    suspend fun getVolumeById(@Path("id") id: String, @Query("key") apiKey: String = ""): VolumeItem
}
