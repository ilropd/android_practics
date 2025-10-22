package com.itb.postapp.network

import android.util.Log
import com.itb.postapp.data.model.Post
import com.itb.postapp.network.PostsApiService.Companion.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface PostsApiService {

     suspend fun getPosts(): List<Post>

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

    override suspend fun getPosts(): List<Post> {
        return try {
            client.get("$BASE_URL/posts").body()
        } catch (e: Exception) {
            Log.e("PostsApiService", "Error in getPosts", e)
            emptyList()
        }
    }
}
