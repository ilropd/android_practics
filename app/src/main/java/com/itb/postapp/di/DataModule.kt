package com.itb.postapp.di

import androidx.room.Room
import com.itb.postapp.data.PostsDatabase
import com.itb.postapp.network.PostsApiService
import com.itb.postapp.network.PostsApiServiceImpl
import com.itb.postapp.repository.PostsRepository
import com.itb.postapp.repository.PostsRepositoryImpl
import com.itb.postapp.ui.viewmodels.PostDetailViewModel
import com.itb.postapp.ui.viewmodels.PostListViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {

    single<PostsApiService> {
        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
        PostsApiServiceImpl(client)
    }

    single {
        Room.databaseBuilder(get(), PostsDatabase::class.java, "posts_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<PostsDatabase>().postsDao() }

    single<PostsRepository> { PostsRepositoryImpl(get(), get()) }

    viewModel { PostListViewModel(get(), get()) }
    viewModel { PostDetailViewModel(get(), get(), get()) }
}