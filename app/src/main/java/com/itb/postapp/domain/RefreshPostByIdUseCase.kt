package com.itb.postapp.domain

import com.itb.postapp.repository.PostsRepository

class RefreshPostByIdUseCase(private val repository: PostsRepository) {
    suspend fun execute(postId: Int) = repository.refreshPostById(postId)
}