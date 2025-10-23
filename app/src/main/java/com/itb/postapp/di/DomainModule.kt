package com.itb.postapp.di

import com.itb.postapp.domain.getByIdUseCase
import com.itb.postapp.domain.GetPostsUseCase
import com.itb.postapp.domain.RefreshPostByIdUseCase
import com.itb.postapp.domain.RefreshPostsUseCase
import org.koin.dsl.module

val domainModule = module {
    single { GetPostsUseCase(get()) }
    single { RefreshPostsUseCase(get()) }
    single { getByIdUseCase(get()) }
    single { RefreshPostByIdUseCase(get()) }
}