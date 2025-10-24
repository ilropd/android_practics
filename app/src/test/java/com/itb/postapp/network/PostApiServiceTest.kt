package com.itb.postapp.network

import androidx.compose.ui.autofill.ContentType
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class PostApiServiceTest {

    @Test
    fun `test getPosts returns deserialized list of PostEntity`() = runTest {

        // Given
        val jsonMock = """
            [
                {"id":1,"userId":11,"title":"Test title 1","body":"Test body 1"},
                {"id":2,"userId":2,"title":"Test title 2","body":"Test body 2"}
            ]
        """.trimIndent()

        val engineMock = MockEngine { request ->
            respond(
                content = jsonMock,
                status = HttpStatusCode.OK,
                headers = headersOf(io.ktor.http.HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(engineMock) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val apiService = PostsApiServiceImpl(client)

        // When
        val posts = apiService.getPosts()

        // Then
        assertThat(posts.size, equalTo(2))

        val firstResult = posts.first()
        assertThat(firstResult.id, equalTo(1))
        assertThat(firstResult.userId, equalTo(11))
        assertThat(firstResult.title, equalTo("Test title 1"))
        assertThat(firstResult.body, equalTo("Test body 1"))

        val secondResult = posts[1]
        assertThat(secondResult.id, equalTo(2))
        assertThat(secondResult.userId, equalTo(2))
        assertThat(secondResult.title, equalTo("Test title 2"))
        assertThat(secondResult.body, equalTo("Test body 2"))
    }

}