package com.urdzik.audiobook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.urdzik.core.ui.theme.AudiobookTheme
import com.urdzik.feature.player.presentation.ui.PlayerRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            AudiobookTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        startDestination = Routes.Player.route,
                        modifier = Modifier.padding(innerPadding),
                        navController = navController
                    ) {
                        composable(Routes.Player.route) {
                            PlayerRoute()
                        }
                    }
                }
            }
        }
    }
}

sealed class Routes(val route: String) {
    data object Player : Routes("player")
}

