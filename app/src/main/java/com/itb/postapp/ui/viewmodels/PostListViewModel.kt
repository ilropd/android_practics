package com.itb.postapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.postapp.data.model.Post
import com.itb.postapp.repository.PostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface PostsListUiState {
    object Loading : PostsListUiState
    data class Success(val posts: List<Post>) : PostsListUiState
    data class Error(val message: String) : PostsListUiState
}

class PostListViewModel(private val repository: PostsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<PostsListUiState>(PostsListUiState.Loading)
    val uiState: StateFlow<PostsListUiState> = _uiState.asStateFlow()

    init {
        Log.d("PostListViewModel", "Initializing PostListViewModel")

        // This collector will react to DB changes
        viewModelScope.launch {
            repository.getPosts()
                .catch { e ->
                    Log.e("PostListViewModel", "DB collection error", e)
                    // This is a critical DB error, not a network error.
                    if (_uiState.value is PostsListUiState.Loading) {
                        _uiState.value = PostsListUiState.Error(e.message ?: "Database error")
                    }
                }
                .collect { posts ->
                    Log.d("PostListViewModel", "DB emitted ${posts.size} posts.")
                    // If we get a non-empty list, we are in a success state.
                    if (posts.isNotEmpty()) {
                        _uiState.value = PostsListUiState.Success(posts)
                    } 
                    // If the list is empty, we don't change the state here.
                    // We let the refresh logic decide if it's an empty success or an error.
                }
        }

        // This coroutine will handle the one-off network refresh
        viewModelScope.launch {
            try {
                Log.d("PostListViewModel", "Starting network refresh.")
                repository.refreshPosts()
                Log.d("PostListViewModel", "Network refresh finished successfully.")
                // If, after a successful refresh, the state is still Loading,
                // it means the DB collector got an empty list. This is a valid, empty success state.
                if (_uiState.value is PostsListUiState.Loading) {
                    _uiState.value = PostsListUiState.Success(emptyList())
                }
            } catch (e: Exception) {
                Log.e("PostListViewModel", "Network refresh failed.", e)
                // If the refresh fails, we should only show an error if we don't already have data.
                if (_uiState.value is PostsListUiState.Loading) {
                    _uiState.value = PostsListUiState.Error(e.message ?: "Network error")
                }
            }
        }
    }
}