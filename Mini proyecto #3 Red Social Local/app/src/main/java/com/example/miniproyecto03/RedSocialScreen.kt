package com.example.miniproyecto03

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun guardarImagenEnAlmacenamientoInterno(context: Context, bitmap: Bitmap): String {
    val filename = "foto_${System.currentTimeMillis()}.jpg"
    val file = File(context.filesDir, filename)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file.absolutePath
}

fun copiarUriAAlmacenamientoInterno(context: Context, uri: Uri): String {
    val filename = "foto_${System.currentTimeMillis()}.jpg"
    val file = File(context.filesDir, filename)
    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file.absolutePath
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedSocialScreen(viewModel: PublicacionViewModel = viewModel()) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fotoRuta by remember { mutableStateOf("") }
    
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var fecha by remember { mutableStateOf(dateFormatter.format(Date())) }

    val publicaciones by viewModel.publicaciones.collectAsState(initial = emptyList())

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            fotoRuta = guardarImagenEnAlmacenamientoInterno(context, bitmap)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fotoRuta = copiarUriAAlmacenamientoInterno(context, uri)
        }
    }
    
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            fecha = dateFormatter.format(selectedCalendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Red Social Local") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Fecha: $fecha", modifier = Modifier.weight(1f))
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                            Text("Tomar Foto")
                        }
                        Button(onClick = { galleryLauncher.launch("image/*") }) {
                            Text("Galería")
                        }
                    }
                    if (fotoRuta.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = File(fotoRuta),
                            contentDescription = null,
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (titulo.isNotBlank() && descripcion.isNotBlank() && fotoRuta.isNotBlank()) {
                                viewModel.insertarPublicacion(titulo, descripcion, fotoRuta, fecha)
                                titulo = ""
                                descripcion = ""
                                fotoRuta = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publicar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(publicaciones) { pub ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)) {
                        Column {
                            if (pub.fotoUri.isNotBlank()) {
                                AsyncImage(
                                    model = File(pub.fotoUri),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = pub.titulo,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { viewModel.eliminarPublicacion(pub) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                    }
                                }
                                Text(
                                    text = pub.fecha,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = pub.descripcion)
                            }
                        }
                    }
                }
            }
        }
    }
}
