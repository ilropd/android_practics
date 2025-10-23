package com.itb.postapp.di

import androidx.room.Room
import com.itb.postapp.data.PostsDatabase
import com.itb.postapp.network.PostsApiService
import com.itb.postapp.repository.PostsRepository
import com.itb.postapp.repository.PostsRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.websocket.WebSocketDeflateExtension.Companion.install
import org.koin.dsl.module

val dataModule = module {
    single {
        HttpClient(PostsApiService) {
            install(ContentNegotiation) { json() }
        }
    }

    single {
        Room.databaseBuilder(get(), PostsDatabase::class.java, "posts_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<PostsDatabase>().postsDao() }

    single<PostsRepository> { PostsRepositoryImpl(get(), get()) }
}