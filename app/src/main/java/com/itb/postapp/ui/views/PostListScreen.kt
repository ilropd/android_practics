package com.itb.postapp.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itb.postapp.ui.viewmodels.PostListViewModel
import com.itb.postapp.ui.viewmodels.PostsListUiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun PostListScreen(
    viewModel: PostListViewModel = koinViewModel(),
    onPostClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    when (uiState) {
        PostsListUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is PostsListUiState.Success -> {
            LazyColumn {
                items((uiState as PostsListUiState.Success).posts) { post ->
                    Text(
                        post.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(),
                                onClick = { onPostClick(post.id) }
                            )
                            .padding(16.dp)
                    )
                    Divider()
                }
            }
        }
        is PostsListUiState.Error -> Text("Error: ${(uiState as PostsListUiState.Error).message}")
    }
}