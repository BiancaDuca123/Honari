package com.honari.app.data.repository

import com.google.gson.GsonBuilder
import com.honari.app.data.remote.BooksApiService
import com.honari.app.data.remote.dto.GoogleBooksResponse
import com.honari.app.data.remote.dto.ImageLinks
import com.honari.app.data.remote.dto.IndustryIdentifier
import com.honari.app.data.remote.dto.VolumeInfo
import com.honari.app.data.remote.dto.VolumeItem
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BookLookupRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: BooksApiService
    private lateinit var repository: BookLookupRepositoryImpl

    private val gson = GsonBuilder().create()

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApiService::class.java)

        repository = BookLookupRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // ── searchByQuery ──────────────────────────────────────────────────────────

    @Test
    fun `searchByQuery returns mapped books on success`() = runTest {
        val fakeResponse = GoogleBooksResponse(
            totalItems = 1,
            items = listOf(
                VolumeItem(
                    id = "abc123",
                    volumeInfo = VolumeInfo(
                        title = "Dune",
                        authors = listOf("Frank Herbert"),
                        averageRating = 4.5,
                        imageLinks = ImageLinks(thumbnail = "http://example.com/dune.jpg")
                    )
                )
            )
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(fakeResponse))
                .addHeader("Content-Type", "application/json")
        )

        val result = repository.searchByQuery("Dune")

        assertTrue(result.isSuccess)
        val books = result.getOrThrow()
        assertEquals(1, books.size)
        assertEquals("abc123", books[0].id)
        assertEquals("Dune", books[0].title)
        assertEquals("Frank Herbert", books[0].author)
        assertEquals(4.5f, books[0].rating)
        // HTTP URLs should be upgraded to HTTPS
        assertEquals("https://example.com/dune.jpg", books[0].imageUrl)
    }

    @Test
    fun `searchByQuery returns empty list when items is null`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(GoogleBooksResponse(totalItems = 0, items = null)))
                .addHeader("Content-Type", "application/json")
        )

        val result = repository.searchByQuery("xyz_not_found")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `searchByQuery returns failure on HTTP error`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(403))

        val result = repository.searchByQuery("Dune")

        assertTrue(result.isFailure)
    }

    // ── searchByIsbn ───────────────────────────────────────────────────────────

    @Test
    fun `searchByIsbn returns book when found`() = runTest {
        val fakeResponse = GoogleBooksResponse(
            totalItems = 1,
            items = listOf(
                VolumeItem(
                    id = "isbn_book",
                    volumeInfo = VolumeInfo(
                        title = "Clean Code",
                        authors = listOf("Robert C. Martin"),
                        industryIdentifiers = listOf(
                            IndustryIdentifier("ISBN_13", "9780132350884")
                        )
                    )
                )
            )
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(fakeResponse))
                .addHeader("Content-Type", "application/json")
        )

        val result = repository.searchByIsbn("9780132350884")

        assertTrue(result.isSuccess)
        assertEquals("isbn_book", result.getOrThrow().id)
        assertEquals("9780132350884", result.getOrThrow().isbn)
    }

    @Test
    fun `searchByIsbn fails when no items returned`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(GoogleBooksResponse(totalItems = 0, items = null)))
                .addHeader("Content-Type", "application/json")
        )

        val result = repository.searchByIsbn("0000000000000")

        assertTrue(result.isFailure)
    }

    // ── getPopularBooks ────────────────────────────────────────────────────────

    @Test
    fun `getPopularBooks returns list of books`() = runTest {
        val fakeResponse = GoogleBooksResponse(
            totalItems = 2,
            items = listOf(
                VolumeItem(id = "p1", volumeInfo = VolumeInfo(title = "Book A")),
                VolumeItem(id = "p2", volumeInfo = VolumeInfo(title = "Book B"))
            )
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(fakeResponse))
                .addHeader("Content-Type", "application/json")
        )

        val result = repository.getPopularBooks()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().size)
    }

    // ── getNewReleases ────────────────────────────────────────────────────────

    @Test
    fun `getNewReleases returns list of books`() = runTest {
        val fakeResponse = GoogleBooksResponse(
            totalItems = 1,
            items = listOf(VolumeItem(id = "nr1", volumeInfo = VolumeInfo(title = "New One")))
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(fakeResponse))
                .addHeader("Content-Type", "application/json")
        )

        val result = repository.getNewReleases()

        assertTrue(result.isSuccess)
        assertEquals("New One", result.getOrThrow().first().title)
    }

    // ── Domain mapping ────────────────────────────────────────────────────────

    @Test
    fun `unknown author falls back to Unknown Author`() = runTest {
        val fakeResponse = GoogleBooksResponse(
            items = listOf(
                VolumeItem(id = "x", volumeInfo = VolumeInfo(title = "Anon", authors = null))
            )
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(fakeResponse))
                .addHeader("Content-Type", "application/json")
        )

        val result = repository.searchByQuery("Anon")
        assertEquals("Unknown Author", result.getOrThrow().first().author)
    }

    @Test
    fun `multiple authors are joined with comma`() = runTest {
        val fakeResponse = GoogleBooksResponse(
            items = listOf(
                VolumeItem(
                    id = "y",
                    volumeInfo = VolumeInfo(title = "Co-written", authors = listOf("Alice", "Bob"))
                )
            )
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(fakeResponse))
                .addHeader("Content-Type", "application/json")
        )

        val result = repository.searchByQuery("co-written")
        assertEquals("Alice, Bob", result.getOrThrow().first().author)
    }

    @Test
    fun `ISBN_13 takes priority over ISBN_10`() = runTest {
        val fakeResponse = GoogleBooksResponse(
            items = listOf(
                VolumeItem(
                    id = "z",
                    volumeInfo = VolumeInfo(
                        title = "ISBN Test",
                        industryIdentifiers = listOf(
                            IndustryIdentifier("ISBN_10", "0132350882"),
                            IndustryIdentifier("ISBN_13", "9780132350884")
                        )
                    )
                )
            )
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(fakeResponse))
                .addHeader("Content-Type", "application/json")
        )

        val result = repository.searchByQuery("isbn test")
        assertEquals("9780132350884", result.getOrThrow().first().isbn)
    }

    @Test
    fun `network failure returns Result failure`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val result = repository.getPopularBooks()

        assertFalse(result.isSuccess)
    }
}
