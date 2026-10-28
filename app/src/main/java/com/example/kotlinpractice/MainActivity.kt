package com.example.kotlinpractice

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.kotlinpractice.ui.AuthScreen
import com.example.kotlinpractice.ui.theme.KotlinPracticeTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            KotlinPracticeTheme {
                AuthScreen()
            }
        }
    }
}
