package com.itb.postapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.itb.postapp.ui.views.PostDetailScreen
import com.itb.postapp.ui.views.PostListScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "posts") {
        composable("posts") { PostListScreen(
            onPostClick = { id -> navController.navigate("postDetail/$id") }) }
        composable(
            "postDetail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.IntType })
        ) { PostDetailScreen(navController = navController) }
    }
}