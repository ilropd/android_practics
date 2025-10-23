package com.itb.postapp.repository

import com.itb.postapp.data.PostsDao
import com.itb.postapp.data.PostsDatabase
import com.itb.postapp.network.PostsApiService
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.itb.postapp.data.model.Post
import com.itb.postapp.data.model.PostDto
import com.itb.postapp.data.model.PostEntity
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.theories.suppliers.TestedOn

class  PostsRepositoryTest {

    private val apiService = mockk<PostsApiService>()
    private val dao = mockk<PostsDao>()
    private lateinit var repository: PostsRepositoryImpl

    @Before
    fun setup() {
        repository = PostsRepositoryImpl(apiService, dao)
    }

    @Test
    fun `test getPosts returns posts from database`() = runTest {

        //Given
        val postsEntity = listOf(
            PostEntity(1, 1, "Title 1", "Body 1"))

        //When
        every { dao.getAll() } returns flowOf(postsEntity)

        //Then
        val result = repository.getPosts().first()

        assertThat(result.size, equalTo(1))
        assertThat(result[0].title, equalTo("Title 1"))
        assertThat(result[0].body, equalTo("Body 1"))

    }

    @Test
    fun `test refreshPosts updates posts in database`() = runTest {

        //Given
        val postsEntity = listOf(PostEntity(1, 1, "Title 1", "Body 1"))

        //When
        coEvery { apiService.getPosts() } returns postsEntity
        coEvery { dao.upsertAll(postsEntity) } just Runs
        repository.refreshPosts()

        //Then
        coVerify { dao.upsertAll(postsEntity) }

         }

    @Test
    fun `refreshPosts updates database with new data from remote repo`() = runTest {
        // Given
        val oldPosts = listOf(PostEntity(1, 1, "Old Title", "Old Body"))
        val newPosts = listOf(PostEntity(1, 1, "New Title", "New Body"))

        // When
        every { dao.getAll() } returns flowOf(oldPosts)
        coEvery { apiService.getPosts() } returns newPosts
        coEvery { dao.upsertAll(newPosts) } just Runs
        repository.refreshPosts()

        // Then
        // They say it's enough to verify that dao.upsertAll was called once with newPosts
        coVerify(exactly = 1) { dao.upsertAll(newPosts) }

        // That's weird, but dao is mock, so it keeps old data
        val result = repository.getPosts().first()
        assertThat(result[0].title, equalTo("Old Title"))
    }


    @Test
    fun `get posts from database if network call fails`() = runTest {

        //Given
        val postsEntity = listOf(PostEntity(1, 1, "Local title", "Local body"))

        //When
        every { dao.getAll() } returns flowOf(postsEntity)
        coEvery { apiService.getPosts() } throws Exception("Network error")

        //Then
        val postsBefore = repository.getPosts().first()
        assertThat(postsBefore.size, equalTo(1))
        assertThat(postsBefore[0].title, equalTo("Local title"))
        assertThat(postsBefore[0].body, equalTo("Local body"))

        try {
            repository.refreshPosts()
        } catch (e: Exception) {
            assertThat(e.message, equalTo("Network error"))
        }

        val postsAfter = repository.getPosts().first()
        assertThat(postsAfter.size, equalTo(1))
        assertThat(postsBefore[0].title, equalTo("Local title"))
        assertThat(postsBefore[0].body, equalTo("Local body"))
    }
}