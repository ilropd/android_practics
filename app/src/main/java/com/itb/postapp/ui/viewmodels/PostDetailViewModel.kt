package com.itb.postapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.postapp.data.model.Post
import com.itb.postapp.repository.PostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import androidx.lifecycle.SavedStateHandle

sealed class PostDetailUiState {
    object Loading : PostDetailUiState()
    data class Success(val post: Post) : PostDetailUiState()
    data class Offline(val message: String) : PostDetailUiState()
    data class Error(val message: String) : PostDetailUiState()
}

class PostDetailViewModel(
    private val repository: PostsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Int = savedStateHandle["postId"] ?: 0

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPostById(postId)
                .catch { _uiState.value = PostDetailUiState.Error(it.message ?: "Error") }
                .collect { it?.let { _uiState.value = PostDetailUiState.Success(it) } }
        }

        viewModelScope.launch {
            try { repository.refreshPosts() }
            catch (_: Exception) { _uiState.value = PostDetailUiState.Offline("Offline") }
        }
    }
}