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

    // Estados de UI (Matrícula recuperada de SharedPreferences)
    var matricula by remember { mutableStateOf(prefsHelper.getLastMatricula()) }
    var nombre by remember { mutableStateOf("") }

    // Opciones y estado para menú desplegable de carreras
    val carreras = listOf(
        "Ingeniería de Software",
        "Ingeniería Civil",
        "Ingeniería en Procesos Industriales",
        "Ingeniería Geodésica",
        "Ingeniería en Nanotecnología"
    )
    var expandedDropdown by remember { mutableStateOf(false) }
    var selectedCarrera by remember { mutableStateOf(carreras[0]) }

    // Opciones y estado para RadioButtons (Turno)
    val turnos = listOf("Matutino", "Vespertino")
    var selectedTurno by remember { mutableStateOf(turnos[0]) }

    // Estado para Switch (Estatus)
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

            // 1. Matrícula
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

            // 3. Carrera (Menú Desplegable)
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

            // 4. Turno (RadioButtons)
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

            // 5. Estatus (Switch)
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

            // 6. Botón de Registro
            Button(
                onClick = {
                    if (matricula.isNotBlank() && nombre.isNotBlank()) {
                        // Persistir matrícula en SharedPreferences
                        prefsHelper.saveLastMatricula(matricula)
                        // Disparar navegación con los datos capturados
                        onNavigateToDetail(matricula, nombre, selectedCarrera, selectedTurno, isActivo)
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