package com.itb.postapp.ui.viewmodels

import app.cash.turbine.test
import com.itb.postapp.data.model.Post
import com.itb.postapp.domain.GetPostsUseCase
import com.itb.postapp.domain.RefreshPostsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class PostListViewModelTest {

    private val getPostsUseCase = mockk<GetPostsUseCase>()
    private val refreshPostsUseCase = mockk<RefreshPostsUseCase>()

    private lateinit var viewModel: PostListViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test flow of state from Loading to Success`() = runTest {

        //Given
        val posts = listOf(Post(1, 1, "Title", "Body"))

        // When
        coEvery { getPostsUseCase.execute() } returns flowOf(posts)
        coEvery { refreshPostsUseCase.execute() } returns Unit

        viewModel = PostListViewModel(getPostsUseCase, refreshPostsUseCase)

        viewModel.uiState.test {
            assertThat(PostsListUiState, equalTo(PostsListUiState.Loading))
            assertThat(PostsListUiState, equalTo(PostsListUiState.Success(posts)))
            assertThat(posts.size, equalTo(1))
            assertThat(posts[0].title, equalTo("Title"))
            assertThat(posts[0].body, equalTo("Body"))
        }

        cancelAndIgnoreRemainingEvents()


        }


    }




}