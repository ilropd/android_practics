package com.itb.postapp.ui.views


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.itb.postapp.data.model.Post
import com.itb.postapp.ui.viewmodels.PostListViewModel
import com.itb.postapp.ui.viewmodels.PostsListUiState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    viewModel: PostListViewModel = koinViewModel(),
    onPostClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var postsCount = (uiState as? PostsListUiState.Success)?.posts?.size ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Posts List   |   Total $postsCount") },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                )
                )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).padding(8.dp)
        ) {
            when (uiState) {
                PostsListUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Loading...",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("loading_text")
                    )
                }

                is PostsListUiState.Success -> {
                    PostListContent(
                        posts = (uiState as PostsListUiState.Success).posts,
                        onPostClick = onPostClick
                    )
                }

                is PostsListUiState.Error -> {
                    Text("Error: ${(uiState as PostsListUiState.Error).message}")
                }
            }
        }
    }
}

@Composable
fun PostListContent(
    posts: List<Post>,
    onPostClick: (Int) -> Unit
) {
    if (posts.isEmpty()) {
        Text("No posts available",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            )
    } else {
        LazyColumn {
            items(posts) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    onClick = { onPostClick(post.id) }
                ) {
                    Text(
                        post.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}