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

@Composable
fun PantallaInicio(navController: NavController) {
    val session = SupabaseClient.client.auth.currentSessionOrNull()
    val user = session?.user
    val scope = rememberCoroutineScope()
    var perfil by remember { mutableStateOf<UsuarioPerfil?>(null) }
    var cargandoPerfil by remember { mutableStateOf(true) }
    val nombreUsuario = user?.userMetadata?.get("nombre")?.toString()?.trim('"') ?: user?.email?.substringBefore("@") ?: "Usuario"

    LaunchedEffect(Unit) {
        user?.id?.let { uid -> perfil = UsuarioRepository.obtenerSocioPorId(uid) }
        cargandoPerfil = false
    }

    if (cargandoPerfil) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) }
    } else {
        Scaffold(bottomBar = { BottomNavBar(navController, perfil?.es_prestador == true) }) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).background(MaterialTheme.colorScheme.background)) {
                Box(modifier = Modifier.fillMaxWidth().height(190.dp).background(Brush.verticalGradient(colors = listOf(OrangeDark, OrangePrimary))).padding(20.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Place, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tecate, Baja California", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                                Text(nombreUsuario.firstOrNull()?.uppercaseChar()?.toString() ?: "U", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Hola, ${nombreUsuario.split(" ").first()}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(if (perfil?.es_prestador == true) "Panel de Socio - ${perfil?.tipo_servicio}" else "Que servicio necesitas?", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = Color.White, shadowElevation = 4.dp) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Buscar profesional...", color = Color.LightGray, fontSize = 14.sp)
                            }
                        }
                    }
                }
                if (perfil?.es_prestador == true) {
                    SeccionAccesosSocio(navController, MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.surfaceVariant)
                } else {
                    SeccionAccesosCliente(navController, LocalContext.current, MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.surfaceVariant)
                }
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Composable fun SeccionAccesosCliente(navController: NavController, context: android.content.Context, sobreFondo: Color, supVar: Color) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text("Categorias", fontWeight = FontWeight.Bold, color = sobreFondo)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(Triple("Plomeria", Icons.Default.Build, "Plomeria"), Triple("Electrico", Icons.Default.Star, "Electricidad"), Triple("Mecanica", Icons.Default.Settings, "Mecanica"), Triple("Mas", Icons.Default.Apps, null)).forEach { (label, icon, cat) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).clickable { navController.navigate("servicios") }) {
                    Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(15.dp)).background(supVar), contentAlignment = Alignment.Center) { Icon(icon, null, tint = OrangePrimary) }
                    Text(label, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable fun SeccionAccesosSocio(navController: NavController, sobreFondo: Color, supVar: Color) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text("Gestion de Socio", fontWeight = FontWeight.Bold, color = sobreFondo)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(Triple("Citas", Icons.Default.DateRange, "socio_citas"), Triple("Historial", Icons.Default.History, "socio_historial"), Triple("Chat", Icons.Default.Chat, "mensajes"), Triple("Perfil", Icons.Default.Person, "perfil")).forEach { (label, icon, ruta) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).clickable { navController.navigate(ruta) }) {
                    Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(15.dp)).background(supVar), contentAlignment = Alignment.Center) { Icon(icon, null, tint = OrangePrimary) }
                    Text(label, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable fun BottomNavBar(navController: NavController, esSocio: Boolean) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val items = if (esSocio) listOf(Triple("inicio", Icons.Default.Dashboard, "Panel"), Triple("socio_citas", Icons.Default.Event, "Citas"), Triple("mensajes", Icons.Default.Chat, "Chats"), Triple("perfil", Icons.Default.Person, "Perfil"))
                    else listOf(Triple("inicio", Icons.Default.Home, "Inicio"), Triple("servicios", Icons.Default.Apps, "Servicios"), Triple("mensajes", Icons.Default.Chat, "Chats"), Triple("perfil", Icons.Default.Person, "Perfil"))
        items.forEach { (ruta, icon, label) ->
            NavigationBarItem(icon = { Icon(icon, null) }, label = { Text(label, fontSize = 10.sp) }, selected = currentRoute == ruta, onClick = { if (currentRoute != ruta) navController.navigate(ruta) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = OrangePrimary.copy(0.1f)))
        }
    }
}
