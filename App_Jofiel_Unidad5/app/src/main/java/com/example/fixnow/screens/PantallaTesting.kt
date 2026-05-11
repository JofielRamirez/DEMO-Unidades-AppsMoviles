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

@Composable fun PantallaTesting(navController: NavController) {
    val scope = rememberCoroutineScope()
    val session = SupabaseClient.client.auth.currentSessionOrNull()
    val uid = session?.user?.id ?: ""
    var perfil by remember { mutableStateOf<UsuarioPerfil?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var esSocio by remember { mutableStateOf(false) }
    
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) { perfil = UsuarioRepository.obtenerSocioPorId(uid); esSocio = perfil?.es_prestador == true }
        cargando = false
    }
    
    Scaffold(bottomBar = { BottomNavBar(navController, esSocio) }) { padding ->
        if (cargando) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) } }
        else { Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).background(MaterialTheme.colorScheme.background)) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Brush.verticalGradient(colors = listOf(OrangeDark, OrangePrimary))).padding(20.dp)) {
                Text("Testing", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomStart))
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Testing", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(8.dp))
                Text("Pantalla funcional del proyecto FixNow.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(100.dp))
        } }
    }
}
