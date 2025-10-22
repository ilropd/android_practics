package com.itb.postapp.ui.views

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itb.postapp.data.model.Post
import com.itb.postapp.ui.viewmodels.PostsListUiState
import com.itb.postapp.ui.viewmodels.PostsScreenModel
import io.ktor.websocket.Frame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsListScreen(
    viewModel: PostsScreenModel,
    uiState: PostsListUiState,
    onPostSelected: (Int) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Frame.Text(text = "Posts") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        ContentPostsScreen(
            uiState,
            modifier = Modifier.padding(innerPadding),
            onPostSelected
        )
    }
}

@Composable
fun ContentPostsScreen(
    uiState: PostsListUiState,
    modifier: Modifier = Modifier,
    onPostSelected: (Int) -> Unit
) {

    when (uiState) {
        is PostsListUiState.Loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Frame.Text(text = "Loading...")
            }

        }

        is PostsListUiState.Success -> {
            if (uiState.posts.isEmpty()) {
                Text(text = "No posts found")
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.posts) { post ->
                        PostRow(
                            post = post,
                            modifier = Modifier.fillMaxWidth(),
                            onPostSelected = onPostSelected
                        )
                    }
                }
            }
        }

        is PostsListUiState.Error -> {
            ErrorScreen(
                message = uiState.message,
                modifier = modifier
            )
        }
    }
}

@Composable
fun PostRow(
    post: Post,
    modifier: Modifier = Modifier,
    onPostSelected: (Int) -> Unit
) {
    Card(
        modifier = modifier,
        onClick = { onPostSelected(post.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}
@Composable
fun ErrorScreen(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}