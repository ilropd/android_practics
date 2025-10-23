package com.itb.postapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.postapp.data.model.PostEntity
import com.itb.postapp.repository.PostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface PostsListUiState {

    object Loading : PostsListUiState

    data class Success(val posts: List<PostEntity>) : PostsListUiState

    data class Error(val message: String) : PostsListUiState
}

class PostListViewModel(private val repository: PostsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<PostsListUiState>(PostsListUiState.Loading)
    val uiState: StateFlow<PostsListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPosts()
                .catch { _uiState.value = PostsListUiState.Error(it.message ?: "Error") }
                .collect { _uiState.value = PostsListUiState.Success(it) }
        }

        viewModelScope.launch {
            try { repository.refreshPosts() } catch (_: Exception) {}
        }
    }
}
