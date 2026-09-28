package com.example.miniproyecto01.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    matricula: String,
    nombre: String,
    carrera: String,
    turno: String,
    activo: Boolean,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmación de Registro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { paddingValues ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Datos del Estudiante Registrado",
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()

                Text(text = "• Matrícula: $matricula", style = MaterialTheme.typography.bodyLarge)
                Text(text = "• Nombre: $nombre", style = MaterialTheme.typography.bodyLarge)
                Text(text = "• Carrera: $carrera", style = MaterialTheme.typography.bodyLarge)
                Text(text = "• Turno: $turno", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "• Estatus: ${if (activo) "Activo" else "Inactivo"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Formulario")
                }
            }
        }
    }
}