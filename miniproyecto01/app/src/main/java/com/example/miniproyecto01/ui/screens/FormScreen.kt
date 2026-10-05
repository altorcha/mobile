package com.example.miniproyecto01.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.miniproyecto01.data.PreferencesHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    onNavigateToDetail: (matricula: String, nombre: String, carrera: String, turno: String, activo: Boolean) -> Unit
) {
    val context = LocalContext.current
    val prefsHelper = remember { PreferencesHelper(context) }

    // Estados de UI
    var matricula by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }

    // Cargar la matrícula guardada en SharedPreferences al iniciar la pantalla
    LaunchedEffect(Unit) {
        val guardada = prefsHelper.getLastMatricula()
        if (guardada.isNotEmpty()) {
            matricula = guardada
        }
    }

    // Opciones del Dropdown de Carreras
    val carreras = listOf(
        "Ingeniería de Software",
        "Ingeniería en Sistemas Computacionales",
        "Ingeniería Civil",
        "Ingeniería Industrial"
    )
    var expandedDropdown by remember { mutableStateOf(false) }
    var selectedCarrera by remember { mutableStateOf(carreras[0]) }

    // Opciones de RadioButtons de Turno
    val turnos = listOf("Matutino", "Vespertino")
    var selectedTurno by remember { mutableStateOf(turnos[0]) }

    // Switch de Estatus
    var isActivo by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Estudiantes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Datos del Estudiante",
                style = MaterialTheme.typography.titleLarge
            )

            // 1. Matrícula (se autocompleta si ya había una guardada)
            OutlinedTextField(
                value = matricula,
                onValueChange = { matricula = it },
                label = { Text("Matrícula") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. Nombre Completo
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 3. Menú Desplegable (ExposedDropdownMenu) para Carrera
            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown }
            ) {
                OutlinedTextField(
                    value = selectedCarrera,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Carrera") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    carreras.forEach { carrera ->
                        DropdownMenuItem(
                            text = { Text(carrera) },
                            onClick = {
                                selectedCarrera = carrera
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            // 4. RadioButtons para Turno
            Text(text = "Selecciona Turno:", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                turnos.forEach { turno ->
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = (turno == selectedTurno),
                                onClick = { selectedTurno = turno }
                            )
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (turno == selectedTurno),
                            onClick = { selectedTurno = turno }
                        )
                        Text(text = turno, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            // 5. Switch para Estatus
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isActivo) "Estatus: Activo" else "Estatus: Inactivo",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isActivo,
                    onCheckedChange = { isActivo = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 6. Botón de Registro y Guardado
            Button(
                onClick = {
                    if (matricula.isNotBlank() && nombre.isNotBlank()) {
                        // Guarda inmediatamente en SharedPreferences
                        prefsHelper.saveLastMatricula(matricula.trim())
                        // Envía los datos hacia la pantalla de detalle
                        onNavigateToDetail(matricula.trim(), nombre.trim(), selectedCarrera, selectedTurno, isActivo)
                    } else {
                        Toast.makeText(context, "Por favor completa la matrícula y el nombre", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Estudiante")
            }
        }
    }
}