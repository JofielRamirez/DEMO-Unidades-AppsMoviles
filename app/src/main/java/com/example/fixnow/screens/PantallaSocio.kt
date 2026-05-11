package com.example.fixnow.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fixnow.data.*
import com.example.fixnow.ui.theme.OrangePrimary
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSocioCitas(navController: NavController) {
    val uid = SupabaseClient.client.auth.currentSessionOrNull()?.user?.id ?: ""
    val scope = rememberCoroutineScope()
    var citas by remember { mutableStateOf<List<Cita>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var tabSeleccionada by remember { mutableIntStateOf(0) }

    LaunchedEffect(uid) { if (uid.isNotEmpty()) { citas = UsuarioRepository.obtenerCitasSocio(uid); cargando = false } }

    Scaffold(bottomBar = { BottomNavBar(navController, esSocio = true) }, topBar = { CenterAlignedTopAppBar(title = { Text("Gestion de Citas", fontWeight = FontWeight.Bold) }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = tabSeleccionada, containerColor = MaterialTheme.colorScheme.surface, contentColor = OrangePrimary) {
                Tab(selected = tabSeleccionada == 0, onClick = { tabSeleccionada = 0 }, text = { Text("Solicitudes") })
                Tab(selected = tabSeleccionada == 1, onClick = { tabSeleccionada = 1 }, text = { Text("Aceptadas") })
            }
            if (cargando) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) } }
            else {
                val cf = when (tabSeleccionada) { 0 -> citas.filter { it.estado == "pendiente" }; 1 -> citas.filter { it.estado == "aceptada" }; else -> emptyList() }
                if (cf.isEmpty()) { Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Icon(Icons.Default.EventNote, null, Modifier.size(64.dp), tint = Color.Gray); Text("Sin citas", color = Color.Gray) } }
                else { LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(cf) { cita -> Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) { Text("Cliente: ${cita.idCliente.take(8)}...", fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f))
                                Surface(color = if (cita.estado == "pendiente") Color(0xFFFFB74D) else Color(0xFF4CAF50), shape = RoundedCornerShape(8.dp)) { Text(cita.estado.uppercase(), Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold) } }
                            Text("Fecha: ${cita.fecha}", fontSize = 14.sp)
                            if (!cita.detalles.isNullOrEmpty()) Text("Detalles: ${cita.detalles}", fontSize = 13.sp, color = Color.Gray)
                            if (cita.estado == "pendiente") Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { scope.launch { UsuarioRepository.actualizarEstadoCita(cita.id ?: "", "cancelada"); citas = UsuarioRepository.obtenerCitasSocio(uid) } }) { Text("Rechazar", color = Color.Red) }
                                Button(onClick = { scope.launch { UsuarioRepository.actualizarEstadoCita(cita.id ?: "", "aceptada"); citas = UsuarioRepository.obtenerCitasSocio(uid) } }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("Aceptar") }
                            }
                        }
                    } }
                } }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSocioHistorial(navController: NavController) {
    val uid = SupabaseClient.client.auth.currentSessionOrNull()?.user?.id ?: ""
    var citas by remember { mutableStateOf<List<Cita>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    LaunchedEffect(uid) { if (uid.isNotEmpty()) { citas = UsuarioRepository.obtenerCitasSocio(uid).filter { it.estado == "completada" || it.estado == "cancelada" }; cargando = false } }
    Scaffold(bottomBar = { BottomNavBar(navController, esSocio = true) }, topBar = { CenterAlignedTopAppBar(title = { Text("Historial", fontWeight = FontWeight.Bold) }) }) { padding ->
        if (cargando) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) } }
        else if (citas.isEmpty()) { Column(Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Icon(Icons.Default.History, null, Modifier.size(64.dp), tint = Color.Gray); Text("No hay historial", color = Color.Gray) } }
        else { LazyColumn(Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(citas) { cita -> Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Column(Modifier.padding(16.dp)) { Text("Trabajo #${cita.id?.takeLast(4)}", fontWeight = FontWeight.Bold); Text("Fecha: ${cita.fecha}"); Text("Estado: ${cita.estado}", color = if (cita.estado == "completada") Color(0xFF1976D2) else Color.Red) } } }
        } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSocioResenas(navController: NavController) {
    Scaffold(bottomBar = { BottomNavBar(navController, esSocio = true) }, topBar = { CenterAlignedTopAppBar(title = { Text("Resenas", fontWeight = FontWeight.Bold) }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Default.Star, null, Modifier.size(64.dp), tint = Color.Gray); Text("No tienes resenas todavia", color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSocioPerfil(navController: NavController) {
    val context = LocalContext.current; val scope = rememberCoroutineScope()
    val uid = SupabaseClient.client.auth.currentSessionOrNull()?.user?.id ?: ""
    var nombre by remember { mutableStateOf("") }; var descripcion by remember { mutableStateOf("") }
    var disponible by remember { mutableStateOf(false) }; var cargando by remember { mutableStateOf(true) }; var guardando by remember { mutableStateOf(false) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val launcherPermisos = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { if (it) { disponible = true; scope.launch { UsuarioRepository.actualizarDisponibilidad(uid, true) } } }

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) { val perfil = UsuarioRepository.obtenerSocioPorId(uid); if (perfil != null) { nombre = perfil.nombre ?: ""; descripcion = perfil.descripcion ?: ""; disponible = perfil.disponible ?: false }; cargando = false }
    }

    LaunchedEffect(disponible) {
        if (disponible) { while (disponible) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try { fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { loc -> loc?.let { scope.launch { UsuarioRepository.actualizarUbicacion(uid, it.latitude, it.longitude) } } } } catch (_: SecurityException) {}
            }; delay(30000)
        } }
    }

    Scaffold(bottomBar = { BottomNavBar(navController, esSocio = true) }, topBar = { CenterAlignedTopAppBar(title = { Text("Mi Panel de Socio", fontWeight = FontWeight.Bold) }) }) { padding ->
        if (cargando) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) } }
        else { Column(Modifier.padding(padding).fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = if (disponible) Color(0xFFE8F5E9) else Color(0xFFF5F5F5))) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(12.dp).clip(CircleShape).background(if (disponible) Color(0xFF4CAF50) else Color.Gray))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) { Text(if (disponible) "ESTAS EN LINEA" else "ESTAS DESCONECTADO", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = if (disponible) Color(0xFF2E7D32) else Color.Gray); Text(if (disponible) "Los clientes pueden encontrarte" else "No apareceras en el mapa", fontSize = 12.sp, color = Color.Gray) }
                    Switch(checked = disponible, onCheckedChange = { nuevo -> if (nuevo) { if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { disponible = true; scope.launch { UsuarioRepository.actualizarDisponibilidad(uid, true) } } else launcherPermisos.launch(Manifest.permission.ACCESS_FINE_LOCATION) } else { disponible = false; scope.launch { UsuarioRepository.actualizarDisponibilidad(uid, false) } } }, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF4CAF50), checkedTrackColor = Color(0xFFA5D6A7)))
                }
            }
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Publico") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripcion de tu servicio") }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(32.dp))
            Button(onClick = { scope.launch { try { guardando = true; UsuarioRepository.actualizarPerfilSocio(uid, nombre, descripcion); Toast.makeText(context, "Perfil guardado", Toast.LENGTH_SHORT).show() } catch (e: Exception) { Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show() } finally { guardando = false } } }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary), enabled = !guardando) {
                if (guardando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp)) else Text("Guardar Cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = { scope.launch { SupabaseClient.client.auth.signOut() } }) { Text("Cerrar Sesion", color = Color.Red) }
        } }
    }
}
