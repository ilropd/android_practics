package com.itb.postapp.domain

import com.itb.postapp.repository.PostsRepository

class GetPostsUseCase(private val repository: PostsRepository) {
    fun execute() = repository.getPosts()
}