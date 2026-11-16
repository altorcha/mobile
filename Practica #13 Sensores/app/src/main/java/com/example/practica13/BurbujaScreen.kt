package com.example.practica13

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BurbujaScreen(sensorManager: SensorManager) {
    val context = LocalContext.current

    // Coordenadas en tiempo real para las tres dimensiones (X, Y, Z)
    var ejeX by remember { mutableFloatStateOf(0f) }
    var ejeY by remember { mutableFloatStateOf(0f) }
    var ejeZ by remember { mutableFloatStateOf(0f) }

    // Desplazamiento de la burbuja en pantalla
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Estados para la detección de sacudida (Shake Detection)
    var sacudidaDetectada by remember { mutableStateOf(false) }
    var contadorSacudidas by remember { mutableIntStateOf(0) }
    var ultimaSacudidaTiempo by remember { mutableLongStateOf(0L) }
    var fuerzaActual by remember { mutableFloatStateOf(0f) }

    // Parámetros del nivel y umbral de sacudida
    val sensibilidad = 50f
    val radioMaximo = 250f
    val umbralSacudida = 12f // Umbral de aceleración neta para detectar sacudida

    // Regresa el estado visual a la normalidad 1.5 segundos después de la sacudida
    LaunchedEffect(sacudidaDetectada, contadorSacudidas) {
        if (sacudidaDetectada) {
            delay(1500)
            sacudidaDetectada = false
        }
    }

    // Animación suave del color de fondo cuando se detecta una sacudida
    val colorFondo by animateColorAsState(
        targetValue = if (sacudidaDetectada) Color(0xFF6A1B29) else Color.DarkGray,
        label = "ColorFondoShake"
    )

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    // 1. Capturamos los valores en tiempo real de los 3 ejes (X, Y, Z)
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]

                    ejeX = x
                    ejeY = y
                    ejeZ = z

                    // 2. Lógica matemática del Nivel de Burbuja (2D)
                    val rawX = -x * sensibilidad
                    val rawY = y * sensibilidad
                    val distancia = sqrt((rawX * rawX) + (rawY * rawY))

                    if (distancia > radioMaximo) {
                        val proporcion = radioMaximo / distancia
                        offsetX = rawX * proporcion
                        offsetY = rawY * proporcion
                    } else {
                        offsetX = rawX
                        offsetY = rawY
                    }

                    // 3. Lógica matemática de Detección de Sacudida (Shake Detection 3D)
                    // Magnitud del vector de aceleración: ||a|| = sqrt(x² + y² + z²)
                    val magnitudVectorial = sqrt((x * x) + (y * y) + (z * z))
                    val aceleracionNeta = abs(magnitudVectorial - SensorManager.GRAVITY_EARTH)
                    fuerzaActual = aceleracionNeta

                    if (aceleracionNeta > umbralSacudida) {
                        val tiempoActual = System.currentTimeMillis()
                        // Cooldown de 800 ms para no contar una sola agitada varias veces
                        if (tiempoActual - ultimaSacudidaTiempo > 800) {
                            ultimaSacudidaTiempo = tiempoActual
                            sacudidaDetectada = true
                            contadorSacudidas++
                            Toast.makeText(
                                context,
                                "¡Sacudida detectada! (#$contadorSacudidas)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(listener, acelerometro, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nivel de Burbuja y Detector Shake") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorFondo),
            contentAlignment = Alignment.Center
        ) {
            // Banner superior de respuesta visual al detectar sacudida
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (sacudidaDetectada) Color(0xFFFF5252) else Color(0xFF2C2C2E)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (sacudidaDetectada) "¡SACUDIDA DETECTADA!" else "Estado: Estable (Sacude el dispositivo)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fuerza neta: ${"%.2f".format(fuerzaActual)} m/s² | Sacudidas: $contadorSacudidas",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }

            // Nivel de Burbuja central
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .border(4.dp, Color.White, CircleShape)
                    .background(Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(50.dp).border(2.dp, Color.Red, CircleShape))

                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .size(40.dp)
                        .background(
                            if (sacudidaDetectada) Color.Yellow else Color(0xFF00FF00),
                            CircleShape
                        )
                )
            }

            // Panel inferior con las 3 coordenadas cartesianas (X, Y, Z)
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Coordenadas del Acelerómetro (m/s²)",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Eje X: ${"%.2f".format(ejeX)}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Eje Y: ${"%.2f".format(ejeY)}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Eje Z: ${"%.2f".format(ejeZ)}", color = Color(0xFF4FC3F7), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}