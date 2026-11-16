package com.example.practica14

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

fun drawableToBitmap(context: Context, drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth.coerceAtLeast(64),
        drawable.intrinsicHeight.coerceAtLeast(64),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

@SuppressLint("MissingPermission")
@Composable
fun MapaOSMScreen() {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var ubicacionActual by remember { mutableStateOf<GeoPoint?>(null) }

    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.marcadorDao()
    val marcadoresGuardados by dao.obtenerMarcadores().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    fun obtenerUbicacionFused() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    ubicacionActual = GeoPoint(location.latitude, location.longitude)
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            ubicacionActual = GeoPoint(lastLoc.latitude, lastLoc.longitude)
                        }
                    }
                }
            }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        permissionGranted = granted
        if (granted) {
            obtenerUbicacionFused()
        }
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            permissionGranted = true
            obtenerUbicacionFused()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.OpenTopo)
                    tileProvider.clearTileCache()
                    setMultiTouchControls(true)
                    controller.setZoom(17.0)
                    controller.setCenter(GeoPoint(25.7905, -108.9875))

                    val eventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false

                        override fun longPressHelper(p: GeoPoint?): Boolean {
                            p?.let { punto ->
                                scope.launch(Dispatchers.IO) {
                                    dao.insertarMarcador(
                                        MarcadorEntity(
                                            latitud = punto.latitude,
                                            longitud = punto.longitude,
                                            titulo = "Marcador (${"%.4f".format(punto.latitude)}, ${"%.4f".format(punto.longitude)})"
                                        )
                                    )
                                }
                                Toast.makeText(ctx, "Marcador guardado en Room", Toast.LENGTH_SHORT).show()
                            }
                            return true
                        }
                    }
                    overlays.add(MapEventsOverlay(eventsReceiver))
                }
            },
            update = { mapView ->
                if (permissionGranted && mapView.overlays.none { it is MyLocationNewOverlay }) {
                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
                    locationOverlay.enableMyLocation()
                    locationOverlay.enableFollowLocation()

                    drawableToBitmap(context, R.drawable.ic_gps_rojo)?.let { bmp ->
                        locationOverlay.setPersonIcon(bmp)
                        locationOverlay.setDirectionArrow(bmp, bmp)
                    }

                    locationOverlay.runOnFirstFix {
                        mapView.post {
                            val currentLocation = locationOverlay.myLocation
                            if (currentLocation != null) {
                                ubicacionActual = currentLocation
                                mapView.controller.setZoom(17.0)
                                mapView.controller.setCenter(currentLocation)
                            }
                        }
                    }
                    mapView.overlays.add(locationOverlay)
                }

                mapView.overlays.removeAll { it is Marker }

                ubicacionActual?.let { pos ->
                    val currentPosMarker = Marker(mapView).apply {
                        position = pos
                        title = "Mi Ubicación Actual (${"%.4f".format(pos.latitude)}, ${"%.4f".format(pos.longitude)})"
                        icon = ContextCompat.getDrawable(context, R.drawable.ic_gps_rojo)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    mapView.overlays.add(currentPosMarker)
                    mapView.controller.setCenter(pos)
                }

                marcadoresGuardados.forEach { item ->
                    val roomMarker = Marker(mapView).apply {
                        position = GeoPoint(item.latitud, item.longitud)
                        title = item.titulo
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    mapView.overlays.add(roomMarker)
                }

                mapView.invalidate()
            }
        )

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xDD1E1E1E))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (permissionGranted) "GPS Activo (Mantén presionado el mapa para guardar puntos)"
                    else "Esperando permiso de ubicación...",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                ubicacionActual?.let { loc ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lat: ${"%.5f".format(loc.latitude)} | Lon: ${"%.5f".format(loc.longitude)} | Guardados: ${marcadoresGuardados.size}",
                        color = Color(0xFF4FC3F7),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}