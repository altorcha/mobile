package com.example.practica14

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        org.osmdroid.config.Configuration.getInstance().load(
            ctx,
            ctx.getSharedPreferences("osmdroid", 0)
        )
        org.osmdroid.config.Configuration.getInstance().userAgentValue = "Practica14_UAS/1.0"

        setContent {
            MapaOSMScreen()
        }
    }
}