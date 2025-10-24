package com.itb.postapp.ui.viewmodels

import app.cash.turbine.test
import com.itb.postapp.data.model.Post
import com.itb.postapp.domain.GetPostsUseCase
import com.itb.postapp.domain.RefreshPostsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


//@RunWith(RobolectricTestRunner::class) is used because in classes of VM I used logger
// but JVM doesn't know what is android.util.Log and falles with error “Method d in android.util.Log not mocked”.
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
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
    fun `test flow of state from Loading to Success on local data`() = runTest {

        // Given
        val posts = listOf(Post(1, 1, "Title", "Body"))

        // When
        every { getPostsUseCase.execute() } returns flowOf(posts)
        coEvery { refreshPostsUseCase.execute() } returns Unit

        viewModel = PostListViewModel(getPostsUseCase, refreshPostsUseCase)


        // PostsListUiState is a sealed class, so we can't use it as a statement
        // So we nee to use awaitItem() instead
        // Then
        viewModel.uiState.test {
            assertThat(awaitItem(), equalTo(PostsListUiState.Loading))
            assertThat(awaitItem(), equalTo(PostsListUiState.Success(posts)))
            assertThat(posts.size, equalTo(1))
            assertThat(posts[0].title, equalTo("Title"))
            assertThat(posts[0].body, equalTo("Body"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test flow of state from Loading to Error (Success with local data)`() = runTest {

        // Given
        val posts = listOf(Post(1, 1, "Title", "Body"))

        // When
        coEvery { refreshPostsUseCase.execute() } throws Exception()
        every { getPostsUseCase.execute() } returns flowOf(posts)

        viewModel = PostListViewModel(getPostsUseCase, refreshPostsUseCase)

        // In reality offline message must be "Show cached data"
        // But this message goes ONLY AFTER network falls with SUCCESS case (according to code)
        // Coroutines in reality acts simultaneously, so refreshPostsUseCase throws exception
        // BEFORE getPostsUseCase gives success state, so getPostsUseCase just rewrite it to null
        // We don't want to change logic of code, so need to change test
        // Then
        viewModel.uiState.test {
            val success = awaitItem() as PostsListUiState.Success
            assertThat(success.posts, equalTo(posts))
            assertThat(success.offlineMessage, equalTo(null))
        }

    }

    @Test
    fun `test flow of state from Loading to Error`() = runTest {

        // Return a flow that throws on collection, so the .catch operator can grab it.
        // When
        every { getPostsUseCase.execute() } returns flow { throw Exception("Database error") }
        coEvery { refreshPostsUseCase.execute() } throws Exception("Network failure")

        viewModel = PostListViewModel(getPostsUseCase, refreshPostsUseCase)

        // Then
        viewModel.uiState.test {
            // Initial state is always Loading
            assertThat(awaitItem(), equalTo(PostsListUiState.Loading))

            // The first coroutine to fail while the state is Loading "wins".
            // Since the DB coroutine is launched first and its error is immediate upon collection,
            // it will set the state to Error("Database error").
            val error = awaitItem() as PostsListUiState.Error
            assertThat(error.message, equalTo("Database error"))

            cancelAndIgnoreRemainingEvents()
        }
    }
}
