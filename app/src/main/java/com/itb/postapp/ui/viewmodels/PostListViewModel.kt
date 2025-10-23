package com.itb.postapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.postapp.data.model.Post
import com.itb.postapp.domain.GetPostsUseCase
import com.itb.postapp.domain.RefreshPostsUseCase
import com.itb.postapp.repository.PostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface PostsListUiState {
    object Loading : PostsListUiState
    data class Success(val posts: List<Post>, val offlineMessage: String? = null) : PostsListUiState
    data class Error(val message: String) : PostsListUiState
}

class PostListViewModel(
    private val getPostsUseCase: GetPostsUseCase,
    private val refreshPostsUseCase: RefreshPostsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostsListUiState>(PostsListUiState.Loading)
    val uiState: StateFlow<PostsListUiState> = _uiState.asStateFlow()

    init {
        Log.d("PostListViewModel", "Initializing PostListViewModel")

        viewModelScope.launch {
            getPostsUseCase.execute()
                .catch { e ->
                    Log.e("PostListViewModel", "DB collection error", e)
                    if (_uiState.value is PostsListUiState.Loading) {
                        _uiState.value = PostsListUiState.Error(e.message ?: "Database error")
                    }
                }
                .collect { posts ->
                    Log.d("PostListViewModel", "DB emitted ${posts.size} posts.")
                    if (posts.isNotEmpty()) {
                        _uiState.value = PostsListUiState.Success(posts)
                    }
                }
        }

        viewModelScope.launch {
            try {
                Log.d("PostListViewModel", "Starting network refresh.")
                refreshPostsUseCase.execute()
                Log.d("PostListViewModel", "Network refresh finished successfully.")

                if (_uiState.value is PostsListUiState.Loading) {
                    _uiState.value = PostsListUiState.Success(emptyList())
                }
            } catch (e: Exception) {
                Log.e("PostListViewModel", "Network refresh failed.", e)

                when (val currentState = _uiState.value) {
                    is PostsListUiState.Success -> {
                        _uiState.value = currentState.copy(
                            offlineMessage = "Show cached data"
                        )
                    }
                    is PostsListUiState.Loading -> {
                        _uiState.value = PostsListUiState.Error(e.message ?: "Network error")
                    }
                    else -> {}
                }
            }
        }
    }
}