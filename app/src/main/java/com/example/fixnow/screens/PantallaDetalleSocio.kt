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

@Composable fun PantallaDetalleSocio(navController: NavController, socioId: String) {
    val context = LocalContext.current; val scope = rememberCoroutineScope()
    val idCliente = SupabaseClient.client.auth.currentSessionOrNull()?.user?.id ?: ""
    var socio by remember { mutableStateOf<UsuarioPerfil?>(null) }; var cargando by remember { mutableStateOf(true) }
    var mostrarDialogoCita by remember { mutableStateOf(false) }
    LaunchedEffect(socioId) { socio = UsuarioRepository.obtenerSocioPorId(socioId); cargando = false }
    Scaffold { padding ->
        if (cargando) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) } }
        else if (socio != null) { Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).background(MaterialTheme.colorScheme.background)) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Brush.verticalGradient(listOf(OrangeDark, OrangePrimary)))) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(16.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) { Text(socio?.nombre ?: "", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White); Text(socio?.tipo_servicio ?: "", color = Color.White.copy(alpha = 0.8f)) }
            }
            Card(modifier = Modifier.padding(20.dp).fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(if (socio?.disponible == true) "Disponible ahora" else "No disponible", fontWeight = FontWeight.Bold, color = if (socio?.disponible == true) Color(0xFF2E7D32) else Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Text(socio?.descripcion ?: "Sin descripcion", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (socio?.resumen_ia != null) { Card(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) { Row { Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Resumen con IA", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2)) }; Spacer(Modifier.height(8.dp)); Text(socio?.resumen_ia ?: "", fontSize = 14.sp, fontStyle = FontStyle.Italic, color = Color(0xFF0D47A1)) }
            } }
            Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { mostrarDialogoCita = true }, modifier = Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(16.dp)) { Icon(Icons.Default.Event, null, tint = OrangePrimary); Spacer(Modifier.width(8.dp)); Text("Agendar", color = OrangePrimary) }
                Button(onClick = { scope.launch { UsuarioRepository.crearCita(idCliente, socioId, SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()), "SERVICIO URGENTE"); Toast.makeText(context, "Solicitud enviada!", Toast.LENGTH_LONG).show() } }, modifier = Modifier.weight(1.5f).height(54.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) { Icon(Icons.Default.FlashOn, null); Spacer(Modifier.width(8.dp)); Text("PEDIR AHORA", fontWeight = FontWeight.ExtraBold) }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = { navController.navigate("chat/$socioId/${socio?.nombre}") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))) { Icon(Icons.Default.Chat, null); Spacer(Modifier.width(8.dp)); Text("Enviar mensaje") }
            Spacer(Modifier.height(100.dp))
        } }
    }
    if (mostrarDialogoCita) { AlertDialog(onDismissRequest = { mostrarDialogoCita = false }, title = { Text("Agendar Cita") }, text = { Text("Funcionalidad de agendar cita con fecha y hora") }, confirmButton = { TextButton(onClick = { mostrarDialogoCita = false }) { Text("OK") } }) }
}
