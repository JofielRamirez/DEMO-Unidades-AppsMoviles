package com.example.fixnow.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fixnow.ui.theme.*
import com.example.fixnow.utils.LocationUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPractica52(navController: NavController) {
    val context = LocalContext.current

    // ── ESTADOS DE CÁMARA ────────────────────────────────────────
    var imagenSeleccionada by remember { mutableStateOf<Uri?>(null) }
    var cantidadFotos by remember { mutableIntStateOf(0) }

    // ── ESTADOS DE GPS ───────────────────────────────────────────
    var latitud by remember { mutableStateOf<Double?>(null) }
    var longitud by remember { mutableStateOf<Double?>(null) }
    var mensajeGPS by remember { mutableStateOf("Presiona para obtener tu ubicación") }
    var rastreando by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Launcher para seleccionar imagen (usa el sensor de cámara indirectamente)
    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imagenSeleccionada = uri
            cantidadFotos++
            Toast.makeText(context, "Imagen seleccionada correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para tomar foto directamente con cámara
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            cantidadFotos++
            Toast.makeText(context, "¡Foto capturada con la cámara!", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePicture.launch(null)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para permiso de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            mensajeGPS = "Obteniendo ubicación..."
            try {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { loc ->
                        loc?.let {
                            latitud = it.latitude
                            longitud = it.longitude
                            mensajeGPS = "Lat: ${"%.6f".format(it.latitude)}, Lng: ${"%.6f".format(it.longitude)}"
                            rastreando = true
                        } ?: run {
                            mensajeGPS = "No se pudo obtener la ubicación"
                        }
                    }
                    .addOnFailureListener {
                        mensajeGPS = "Error: ${it.message}"
                    }
            } catch (e: SecurityException) {
                mensajeGPS = "Error de permisos"
            }
        } else {
            mensajeGPS = "Permiso de ubicación denegado"
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = Brush.horizontalGradient(listOf(OrangeDark, OrangePrimary)))
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Práctica 5.2", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Implementación de Sensores", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ══════════════════════════════════════════════════════
            //  SECCIÓN 1: SENSOR DE CÁMARA
            // ══════════════════════════════════════════════════════
            Text(
                "Sensor de Cámara",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Implementación basada en el módulo de subida de fotos de FixNow. " +
                "Los socios usan esta funcionalidad para publicar fotos de sus trabajos y actualizar su perfil.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
            )

            // Card de cámara
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Vista previa de imagen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagenSeleccionada != null) {
                            AsyncImage(
                                model = imagenSeleccionada,
                                contentDescription = "Foto seleccionada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Image,
                                    null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Sin imagen seleccionada",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón: Seleccionar de galería (como en FixNow)
                        Button(
                            onClick = {
                                pickMedia.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Galería", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        // Botón: Tomar foto con cámara
                        OutlinedButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED
                                ) {
                                    takePicture.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangePrimary)
                        ) {
                            Icon(Icons.Default.CameraAlt, null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cámara", fontSize = 13.sp, color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Estadísticas
                    Surface(
                        color = OrangePrimary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(cantidadFotos.toString(), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = OrangePrimary)
                                Text("Fotos capturadas", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    if (imagenSeleccionada != null) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    null,
                                    tint = if (imagenSeleccionada != null) Color(0xFF4CAF50) else Color.Gray,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text("Estado", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ══════════════════════════════════════════════════════
            //  SECCIÓN 2: SENSOR GPS
            // ══════════════════════════════════════════════════════
            Text(
                "Sensor GPS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Implementación basada en el sistema de seguimiento en tiempo real de FixNow. " +
                "Se utiliza FusedLocationProviderClient para obtener la ubicación precisa del usuario.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
            )

            // Card de GPS
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (rastreando) Color(0xFF4CAF50) else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (rastreando) "GPS ACTIVO" else "GPS INACTIVO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (rastreando) Color(0xFF2E7D32) else Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Info de ubicación
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Ubicación actual:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                mensajeGPS,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (latitud != null && longitud != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Latitud", fontSize = 11.sp, color = Color.Gray)
                                        Text("${"%.6f".format(latitud)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Longitud", fontSize = 11.sp, color = Color.Gray)
                                        Text("${"%.6f".format(longitud)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de ubicación
                    Button(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED
                            ) {
                                mensajeGPS = "Obteniendo ubicación..."
                                try {
                                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                                        .addOnSuccessListener { loc ->
                                            loc?.let {
                                                latitud = it.latitude
                                                longitud = it.longitude
                                                mensajeGPS = "Lat: ${"%.6f".format(it.latitude)}, Lng: ${"%.6f".format(it.longitude)}"
                                                rastreando = true
                                            } ?: run {
                                                mensajeGPS = "No se pudo obtener la ubicación"
                                            }
                                        }
                                } catch (e: SecurityException) {
                                    mensajeGPS = "Error de permisos"
                                }
                            } else {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        )
                    ) {
                        Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Obtener Mi Ubicación", fontWeight = FontWeight.Bold)
                    }

                    // Mapa si hay ubicación
                    if (latitud != null && longitud != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            val mapView = remember { MapView(context) }
                            AndroidView(
                                factory = { mapView.apply { onCreate(null); onResume() } },
                                modifier = Modifier.fillMaxSize(),
                                update = { mv ->
                                    mv.getMapAsync { map ->
                                        map.uiSettings.isZoomControlsEnabled = true
                                        val pos = LatLng(latitud!!, longitud!!)
                                        map.clear()
                                        map.addMarker(
                                            MarkerOptions()
                                                .position(pos)
                                                .title("Mi ubicación actual")
                                        )
                                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Distancia a un punto de referencia (Tecate)
                        val distTecate = LocationUtils.calcularDistancia(
                            latitud!!, longitud!!, 32.5727, -116.6262
                        )
                        Surface(
                            color = Color(0xFF1976D2).copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.SocialDistance, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Distancia a Tecate, B.C.", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        LocationUtils.formatoDistancia(distTecate),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1976D2)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nota sobre la implementación
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Esta implementación está basada directamente en el código de FixNow: " +
                        "la selección de imágenes replica el módulo de PantallaSocioPerfil y PantallaPerfil, " +
                        "mientras que el GPS replica el sistema de PantallaServicios y PantallaSeguimientoSocio.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF0D47A1)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
