package com.itb.postapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.postapp.data.model.Post
import com.itb.postapp.repository.PostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PostsListUiState {

    object Loading : PostsListUiState

    data class Success(val posts: List<Post>) : PostsListUiState

    data class Error(val message: String) : PostsListUiState
}

class PostsScreenModel (
    private val postsRepository: PostsRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<PostsListUiState>(PostsListUiState.Loading)
    val uiState: StateFlow<PostsListUiState> = _uiState.asStateFlow()

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()

    init { getPosts() }

    fun getPosts() {
        viewModelScope.launch {
            _uiState.value = PostsListUiState.Loading
            try {
                val posts = postsRepository.getPosts()
                _uiState.value = PostsListUiState.Success(posts)
            }
            catch(e: Exception) {
                _uiState.value = PostsListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectPost(postId: Int) {
        val currentUiState = _uiState.value

        if (currentUiState is PostsListUiState.Success) {
            _selectedPost.value = currentUiState.posts.find { it.id == postId }
        } else {
            println("Invalid UI state for $postId")
        }
    }
}
