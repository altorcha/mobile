package com.example.miniproyecto02

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen() {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).tareaDao() }
    val coroutineScope = rememberCoroutineScope()
    
    val tareas by dao.obtenerTodas().collectAsState(initial = emptyList())
    
    var filtro by remember { mutableStateOf("Todas") }
    var sortByState by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    
    var showAddEditDialog by remember { mutableStateOf(false) }
    var tareaEnEdicion by remember { mutableStateOf<Tarea?>(null) }
    
    val tareasFiltradas = tareas.filter { 
        when (filtro) {
            "Pendientes" -> !it.completada
            "Completadas" -> it.completada
            else -> true
        }
    }.let { list ->
        if (sortByState) {
            list.sortedBy { it.completada }
        } else {
            list 
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrador ($filtro)") },
                actions = {
                    IconButton(onClick = {
                        sortByState = !sortByState
                        Toast.makeText(context, if (sortByState) "Orden: Estado" else "Orden: Fecha", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Ordenar")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Ver: Todas") }, onClick = { filtro = "Todas"; showMenu = false })
                        DropdownMenuItem(text = { Text("Ver: Pendientes") }, onClick = { filtro = "Pendientes"; showMenu = false })
                        DropdownMenuItem(text = { Text("Ver: Completadas") }, onClick = { filtro = "Completadas"; showMenu = false })
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Borrar completadas") },
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    dao.eliminarCompletadas()
                                }
                                Toast.makeText(context, "Completadas borradas", Toast.LENGTH_SHORT).show()
                                showMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                tareaEnEdicion = null
                showAddEditDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Tarea")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tareasFiltradas, key = { it.id }) { tarea ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tarea.completada,
                            onCheckedChange = { isChecked ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    dao.actualizar(tarea.copy(completada = isChecked))
                                }
                            }
                        )
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                            Text(
                                text = tarea.titulo,
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                            )
                            if (tarea.descripcion.isNotBlank()) {
                                Text(
                                    text = tarea.descripcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(tarea.fechaCreacion))
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = {
                            tareaEnEdicion = tarea
                            showAddEditDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                dao.eliminar(tarea)
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            }
        }
    }
    
    if (showAddEditDialog) {
        var titulo by remember { mutableStateOf(tareaEnEdicion?.titulo ?: "") }
        var descripcion by remember { mutableStateOf(tareaEnEdicion?.descripcion ?: "") }
        
        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = { Text(if (tareaEnEdicion == null) "Nueva Tarea" else "Editar Tarea") },
            text = {
                Column {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titulo.isNotBlank()) {
                            coroutineScope.launch(Dispatchers.IO) {
                                if (tareaEnEdicion == null) {
                                    dao.insertar(Tarea(titulo = titulo, descripcion = descripcion))
                                } else {
                                    dao.actualizar(tareaEnEdicion!!.copy(titulo = titulo, descripcion = descripcion))
                                }
                            }
                            showAddEditDialog = false
                        }
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showAddEditDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
