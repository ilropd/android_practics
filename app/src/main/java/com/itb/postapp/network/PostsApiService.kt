package com.itb.postapp.network

import android.util.Log
import com.itb.postapp.data.model.PostEntity
import com.itb.postapp.network.PostsApiService.Companion.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface PostsApiService {

     suspend fun getPosts(): List<PostEntity>

     suspend fun getPostById(postId: Int): PostEntity?

     companion object {

         internal const val BASE_URL = "https://jsonplaceholder.typicode.com"
         
         fun create(): PostsApiService {
             return PostsApiServiceImpl(
                 client = HttpClient(OkHttp) {
                     install(ContentNegotiation) {
                         json(Json {
                             prettyPrint = true
                             isLenient = true
                             ignoreUnknownKeys = true
                         }
                         )
                     }
                     }
             )
         }
     }
 }

class PostsApiServiceImpl(private val client: HttpClient) : PostsApiService {

    override suspend fun getPosts(): List<PostEntity> {
        return client.get("$BASE_URL/posts").body()
    }

    override suspend fun getPostById(postId: Int): PostEntity? {
        return client.get("$BASE_URL/posts/$postId").body()
    }
}
