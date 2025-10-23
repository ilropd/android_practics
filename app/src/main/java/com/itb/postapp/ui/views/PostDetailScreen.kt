package com.itb.postapp.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itb.postapp.ui.viewmodels.PostDetailUiState
import com.itb.postapp.ui.viewmodels.PostDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        PostDetailUiState.Loading -> CircularProgressIndicator()
        is PostDetailUiState.Success -> {
            val post = (uiState as PostDetailUiState.Success).post
            Column(Modifier.padding(16.dp)) {
                Text(post.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(post.body)
            }
        }
        is PostDetailUiState.Offline -> Text("Offline: ${(uiState as PostDetailUiState.Offline).message}")
        is PostDetailUiState.Error -> Text("Error: ${(uiState as PostDetailUiState.Error).message}")
    }
}