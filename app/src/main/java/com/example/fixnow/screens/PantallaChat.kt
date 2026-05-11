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

@Composable fun PantallaChat(navController: NavController, socioId: String, socioNombre: String) {
    val scope = rememberCoroutineScope()
    val miId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: ""
    var textoMensaje by remember { mutableStateOf("") }
    val listaChatUI = remember { mutableStateListOf<MensajeDB>() }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(socioId) {
        ChatRepository.marcarComoLeidos(miId, socioId)
        val historial = ChatRepository.obtenerMensajesHistoricos(miId, socioId)
        listaChatUI.clear(); listaChatUI.addAll(historial); cargando = false
    }

    Scaffold(topBar = {
        Box(modifier = Modifier.fillMaxWidth().background(brush = Brush.horizontalGradient(colors = listOf(OrangeDark, OrangePrimary))).statusBarsPadding().padding(horizontal = 8.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                Text(socioNombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        }
    }, bottomBar = {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp).navigationBarsPadding().imePadding(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = textoMensaje, onValueChange = { textoMensaje = it }, modifier = Modifier.weight(1f), placeholder = { Text("Escribe un mensaje...") }, shape = RoundedCornerShape(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(onClick = {
                    if (textoMensaje.isNotBlank()) {
                        val texto = textoMensaje; textoMensaje = ""
                        val temporal = MensajeDB(id = "temp_${System.currentTimeMillis()}", idEmisor = miId, idReceptor = socioId, contenido = texto, createdAt = "Z")
                        listaChatUI.add(temporal)
                        scope.launch { try { ChatRepository.enviarMensaje(miId, socioId, texto) } catch (e: Exception) { listaChatUI.remove(temporal) } }
                    }
                }) { Icon(Icons.AutoMirrored.Filled.Send, null, tint = OrangePrimary) }
            }
        }
    }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaChatUI, key = { it.id }) { msj ->
                val esMio = msj.idEmisor == miId
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (esMio) Arrangement.End else Arrangement.Start) {
                    Box(modifier = Modifier.widthIn(max = 280.dp).clip(RoundedCornerShape(18.dp)).background(if (esMio) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text(msj.contenido, color = if (esMio) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
