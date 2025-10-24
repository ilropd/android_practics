package com.itb.postapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.itb.postapp.ui.navigation.AppNavHost
import com.itb.postapp.ui.theme.PostAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PostAppTheme {
                SetupSystemBars()
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}


@Composable
fun SetupSystemBars() {
    val view = LocalView.current
    val window = (view.context as Activity).window

    SideEffect {
        window.statusBarColor = Color.Transparent.toArgb()

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
}

