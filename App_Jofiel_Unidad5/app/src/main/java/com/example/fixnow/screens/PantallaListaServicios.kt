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

@Composable fun PantallaListaServicios(navController: NavController, categoria: String) {
    var socios by remember { mutableStateOf<List<UsuarioPerfil>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(categoria) { scope.launch { socios = UsuarioRepository.obtenerSociosPorCategoria(categoria); cargando = false } }
    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().background(BackgroundWhite).padding(padding)) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(brush = Brush.verticalGradient(colors = listOf(OrangePrimary, OrangeLight)))) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                Text(text = categoria, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            }
            if (cargando) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) } }
            else if (socios.isEmpty()) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay socios en esta categoria.", color = Color.Gray) } }
            else { LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(socios) { socio -> Card(modifier = Modifier.fillMaxWidth().clickable { navController.navigate("detalle_socio/${socio.id}") }, shape = RoundedCornerShape(16.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp).clip(CircleShape).background(OrangePrimary.copy(alpha = 0.1f))) { Text(socio.nombre?.take(1)?.uppercase() ?: "S", color = OrangePrimary, fontSize = 28.sp) }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column { Text(socio.nombre ?: "Socio", fontWeight = FontWeight.Bold, fontSize = 18.sp); Text(socio.tipo_servicio ?: "Servicio", fontSize = 12.sp, color = Color.Gray) }
                    }
                } }
            } }
        }
    }
}
