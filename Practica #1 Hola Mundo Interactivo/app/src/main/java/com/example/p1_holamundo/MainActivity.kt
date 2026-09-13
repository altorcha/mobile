package com.example.p1_holamundo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.p1_holamundo.ui.theme.P1_HolaMundoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            P1_HolaMundoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    HolaMundoInteractivo(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HolaMundoInteractivo(modifier: Modifier = Modifier) {

    var nombre by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("¡Hola!") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = mensaje,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )


        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Ingresa tu nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )


        Button(onClick = {
            mensaje = if (nombre.isNotBlank()) "¡Hola, $nombre!" else "¡Hola, Mundo!"
        }) {
            Text("Saludar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HolaMundoPreview() {
    P1_HolaMundoTheme {
        HolaMundoInteractivo()
    }
}