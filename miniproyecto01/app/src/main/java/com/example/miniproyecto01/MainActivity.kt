package com.example.miniproyecto01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.miniproyecto01.navigation.AppNavigation
import com.example.miniproyecto01.ui.theme.Miniproyecto01Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Miniproyecto01Theme {
                AppNavigation()
            }
        }
    }
}