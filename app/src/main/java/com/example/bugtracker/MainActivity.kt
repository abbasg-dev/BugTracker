package com.example.bugtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bugtracker.ui.screens.MainScreen
import com.example.bugtracker.ui.theme.BugTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BugTrackerTheme {
                MainScreen()
            }
        }
    }
}