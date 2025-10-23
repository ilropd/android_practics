package com.itb.postapp.domain

import com.itb.postapp.repository.PostsRepository

class getByIdUseCase(private val repository: PostsRepository) {
    fun execute(postId: Int) = repository.getById(postId)
}