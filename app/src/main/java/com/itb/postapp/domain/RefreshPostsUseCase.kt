package com.itb.postapp.domain

import com.itb.postapp.repository.PostsRepository

class RefreshPostsUseCase(private val repository: PostsRepository) {
    suspend fun execute() = repository.refreshPosts()
}