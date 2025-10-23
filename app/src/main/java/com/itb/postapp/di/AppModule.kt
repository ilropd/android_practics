package com.itb.postapp.di

import androidx.lifecycle.SavedStateHandle
import com.itb.postapp.ui.viewmodels.PostDetailViewModel
import com.itb.postapp.ui.viewmodels.PostListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { PostListViewModel(
        get(),
        get()) }

    viewModel { (handle: SavedStateHandle) ->
        PostDetailViewModel(
        get(),
        get(),
        handle) }
}