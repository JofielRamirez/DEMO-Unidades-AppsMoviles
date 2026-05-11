package com.example.fixnow.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.fixnow.TemaApp
import com.example.fixnow.ui.theme.*
import com.example.fixnow.data.*
import com.example.fixnow.utils.LocationUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.isSystemInDarkTheme

@Composable fun PantallaSeguimientoSocio(navController: NavController, citaId: String) {
    val context = LocalContext.current
    var cita by remember { mutableStateOf<Cita?>(null) }; var socio by remember { mutableStateOf<UsuarioPerfil?>(null) }; var cargando by remember { mutableStateOf(true) }
    LaunchedEffect(citaId) { cita = UsuarioRepository.obtenerCitaPorId(citaId); cita?.let { socio = UsuarioRepository.obtenerSocioPorId(it.idSocio) }; cargando = false }
    Scaffold(topBar = { TopAppBar(title = { Text("Socio en camino", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { padding ->
        if (cargando) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) } }
        else { Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Default.LocationOn, null, tint = OrangePrimary, modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(16.dp))
            Text(socio?.nombre ?: "Socio", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(socio?.tipo_servicio ?: "", color = Color.Gray)
            Spacer(Modifier.height(24.dp))
            Text("Estado: ${cita?.estado?.uppercase()}", fontWeight = FontWeight.Bold, color = OrangePrimary)
            if (socio?.latitud != null && cita?.latCliente != null) {
                val dist = LocationUtils.calcularDistancia(socio!!.latitud!!, socio!!.longitud!!, cita!!.latCliente!!, cita!!.lonCliente!!)
                Spacer(Modifier.height(16.dp))
                Text("Distancia: ${LocationUtils.formatoDistancia(dist)}", fontSize = 18.sp)
            }
        } }
    }
}
