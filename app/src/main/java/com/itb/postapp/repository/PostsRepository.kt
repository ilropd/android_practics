package com.itb.postapp.repository

import com.itb.postapp.data.PostsDao
import com.itb.postapp.data.model.Post
import com.itb.postapp.data.model.PostEntity
import com.itb.postapp.network.PostsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    override fun getPosts(): Flow<List<Post>> =
        dao.getAllPosts().map { list -> list.map { it.toDomain() } }

    override suspend fun refreshPosts() {
        val postsDto = apiService.getPosts()
        val entities = postsDto.map {
            PostEntity(
                id = it.id,
                userId = it.userId,
                title = it.title,
                body = it.body
            )
        }
        dao.insertPosts(entities)
    }

    override fun getPostById(postId: Int): Flow<Post?> =
        dao.getPostById(postId).map { it?.toDomain() }

    override suspend fun refreshPostById(postId: Int) {
        val dto = apiService.getPostById(postId)
        val entity = PostEntity(
            id = dto!!.id,
            userId = dto.userId,
            title = dto.title,
            body = dto.body
        )
        dao.insertPosts(listOf(entity))
    }

    private fun PostEntity.toDomain(): Post = Post(
        id = id,
        userId = userId,
        title = title,
        body = body
    )
}