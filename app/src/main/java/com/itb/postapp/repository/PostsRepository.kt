package com.itb.postapp.repository

import com.itb.postapp.data.PostsDao
import com.itb.postapp.data.model.Post
import com.itb.postapp.network.PostsApiService
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    fun getPosts(): Flow<List<Post>>
    suspend fun refreshPosts()

    fun getPostById(postId: Int): Flow<Post?>
    suspend fun refreshPostById(postId: Int)
}

class PostsRepositoryImpl(
    private val apiService: PostsApiService,
    private val dao: PostsDao
) : PostsRepository {

    override fun getPosts(): Flow<List<Post>> = dao.getAllPosts()

    override suspend fun refreshPosts() {
        try {
            val postsDto = apiService.getPosts()
            val postsEntities = postsDto.map { it ->
                Post(
                    id = it.id,
                    userId = it.userId,
                    title = it.title,
                    body = it.body
                )
            }
            dao.insertPosts(postsEntities)
        } catch (e: Exception) {}
    }

    override fun getPostById(postId: Int): Flow<Post?> = dao.getPostById(postId)

    override suspend fun refreshPostById(postId: Int) {
        try {
            val postDto = apiService.getPostById(postId)
            val postEntity = Post(
                id = postDto!!.id,
                userId = postDto.userId,
                title = postDto.title,
                body = postDto.body
            )
            dao.insertPosts(listOf(postEntity))
        } catch (e: Exception) {}
    }
}