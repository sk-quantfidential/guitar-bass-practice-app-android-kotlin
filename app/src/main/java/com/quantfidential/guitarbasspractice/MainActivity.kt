package com.quantfidential.guitarbasspractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quantfidential.guitarbasspractice.presentation.ui.screens.MainScreen
import com.quantfidential.guitarbasspractice.presentation.ui.theme.GuitarBassPracticeAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            GuitarBassPracticeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GuitarBassPracticeApp()
                }
            }
        }
    }
}

@Composable
fun GuitarBassPracticeApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController = navController)
        }
        
        // Future screens can be added here
        // composable("exercise_detail/{exerciseId}") { backStackEntry ->
        //     ExerciseDetailScreen(exerciseId = backStackEntry.arguments?.getString("exerciseId"))
        // }
        
        // composable("profile_settings") {
        //     ProfileSettingsScreen(navController = navController)
        // }
    }
}