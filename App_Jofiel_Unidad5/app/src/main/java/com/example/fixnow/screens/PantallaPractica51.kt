package com.example.fixnow.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fixnow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPractica51(navController: NavController) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }

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
                        Text("Práctica 5.1", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Investigación de Sensores", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = tabSeleccionada,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = OrangePrimary
            ) {
                Tab(
                    selected = tabSeleccionada == 0,
                    onClick = { tabSeleccionada = 0 },
                    text = { Text("Cámara", fontSize = 13.sp) },
                    icon = { Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = tabSeleccionada == 1,
                    onClick = { tabSeleccionada = 1 },
                    text = { Text("GPS", fontSize = 13.sp) },
                    icon = { Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(18.dp)) }
                )
            }

            // Contenido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (tabSeleccionada == 0) {
                    ContenidoCamara()
                } else {
                    ContenidoGPS()
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ContenidoCamara() {
    // ══════════════════════════════════════════════════════════════
    //  SENSOR: CÁMARA
    // ══════════════════════════════════════════════════════════════

    SeccionInvestigacion(
        icono = Icons.Default.CameraAlt,
        titulo = "Sensor de Cámara",
        color = Color(0xFF1976D2)
    )

    // Funcionamiento
    CardInvestigacion(
        titulo = "Funcionamiento",
        icono = Icons.Default.Settings,
        color = Color(0xFF1976D2)
    ) {
        Text(
            text = "La cámara en dispositivos Android funciona a través del framework Camera2 API (o CameraX como capa de abstracción). " +
                    "El proceso involucra varios componentes clave:",
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        PuntoInvestigacion("Sensor de imagen (CMOS): Captura la luz a través de millones de fotodiodos que convierten fotones en señales eléctricas. Cada píxel del sensor corresponde a un fotodiodo con un filtro de color (patrón Bayer).")
        PuntoInvestigacion("Procesamiento ISP: El Image Signal Processor convierte los datos RAW del sensor en una imagen procesada, aplicando balance de blancos, reducción de ruido, HDR y corrección de lente.")
        PuntoInvestigacion("Pipeline de captura: Android maneja la cámara mediante sesiones de captura. Se configura una CaptureRequest con parámetros (ISO, exposición, enfoque) y se envía al dispositivo de cámara.")
        PuntoInvestigacion("Superficies de salida: Las imágenes capturadas se envían a superficies configuradas como SurfaceView (vista previa), ImageReader (procesamiento) o MediaRecorder (video).")
        PuntoInvestigacion("Autofocus y exposición: El sistema utiliza algoritmos PDAF (Phase Detection) o CDAF (Contrast Detection) para enfocar automáticamente, midiendo el contraste o la fase de la luz en diferentes regiones.")
    }

    // Casos de uso
    CardInvestigacion(
        titulo = "Casos de Uso",
        icono = Icons.Default.Apps,
        color = Color(0xFF388E3C)
    ) {
        Text(
            text = "La cámara es uno de los sensores más versátiles del dispositivo. En nuestra aplicación FixNow se utiliza específicamente para:",
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        PuntoInvestigacion("Subida de fotos de perfil: Los socios (prestadores de servicio) pueden tomar o seleccionar una foto desde la galería para establecer su foto de perfil profesional.")
        PuntoInvestigacion("Galería de trabajos realizados: Los socios suben fotografías de sus trabajos completados para que los clientes puedan ver la calidad de su servicio antes de contratarlos.")
        PuntoInvestigacion("Documentos de verificación: Durante el registro como socio, se capturan fotos de la identificación oficial (INE/Pasaporte) y carta de antecedentes no penales para verificación de identidad.")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Otros casos de uso comunes en la industria:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        PuntoInvestigacion("Lectura de códigos QR/barras para pagos o inventario.")
        PuntoInvestigacion("Realidad aumentada (AR) para visualizar productos en espacios reales.")
        PuntoInvestigacion("OCR (reconocimiento de texto) para digitalizar documentos.")
        PuntoInvestigacion("Videollamadas y comunicación visual en tiempo real.")
    }

    // Permisos requeridos
    CardInvestigacion(
        titulo = "Permisos Requeridos",
        icono = Icons.Default.Security,
        color = Color(0xFFE65100)
    ) {
        Text(
            text = "Android requiere permisos explícitos para acceder a la cámara. Estos se declaran en el AndroidManifest.xml y se solicitan en tiempo de ejecución:",
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        CodigoPermiso("android.permission.CAMERA", "Permiso peligroso (runtime). Permite acceder directamente al hardware de la cámara para capturar fotos y video.")
        CodigoPermiso("android.hardware.camera", "Feature declaration (uses-feature). Indica que la app usa la cámara. Con required=\"false\" la app funciona sin cámara.")
        CodigoPermiso("android.permission.WRITE_EXTERNAL_STORAGE", "Necesario en Android < 10 para guardar fotos en almacenamiento externo. En Android 10+ se usa Scoped Storage.")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "En FixNow utilizamos el contrato PickVisualMedia de AndroidX (Photo Picker) que NO requiere permiso de cámara ya que delega la selección al sistema operativo, " +
                    "proporcionando una experiencia más segura y privada para el usuario.",
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ContenidoGPS() {
    // ══════════════════════════════════════════════════════════════
    //  SENSOR: GPS
    // ══════════════════════════════════════════════════════════════

    SeccionInvestigacion(
        icono = Icons.Default.LocationOn,
        titulo = "Sensor de GPS",
        color = Color(0xFF388E3C)
    )

    // Funcionamiento
    CardInvestigacion(
        titulo = "Funcionamiento",
        icono = Icons.Default.Settings,
        color = Color(0xFF1976D2)
    ) {
        Text(
            text = "El GPS (Global Positioning System) es un sistema de navegación por satélite que permite determinar la ubicación geográfica del dispositivo con alta precisión:",
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        PuntoInvestigacion("Constelación de satélites: El sistema GPS consta de al menos 24 satélites orbitando la Tierra a ~20,200 km de altitud. El receptor del teléfono necesita señales de mínimo 4 satélites para calcular su posición tridimensional.")
        PuntoInvestigacion("Trilateración: El receptor mide el tiempo que tarda la señal de radio (velocidad de la luz) en llegar desde cada satélite. Con las distancias a 3+ satélites, calcula la intersección de esferas para determinar latitud, longitud y altitud.")
        PuntoInvestigacion("A-GPS (Assisted GPS): Los dispositivos móviles usan datos de la red celular y Wi-Fi para acelerar el \"Time to First Fix\" (TTFF), reduciendo el tiempo de localización de minutos a segundos.")
        PuntoInvestigacion("Fused Location Provider: En Android, Google Play Services combina GPS, Wi-Fi, Bluetooth y sensores inerciales (acelerómetro, giroscopio) para ofrecer ubicación precisa y eficiente en batería a través de la FusedLocationProviderClient API.")
        PuntoInvestigacion("Precisión: GPS estándar ofrece ~3-5 metros de precisión. Con correcciones SBAS/WAAS puede mejorar a ~1-2 metros. Algunos dispositivos modernos soportan GPS de doble frecuencia (L1+L5) para precisión submétrica.")
    }

    // Casos de uso
    CardInvestigacion(
        titulo = "Casos de Uso",
        icono = Icons.Default.Apps,
        color = Color(0xFF388E3C)
    ) {
        Text(
            text = "El GPS es fundamental en nuestra aplicación FixNow. Se implementa en las siguientes funcionalidades:",
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        PuntoInvestigacion("Búsqueda por proximidad: Cuando un cliente solicita un servicio inmediato, la app obtiene su ubicación GPS y ordena a los socios disponibles por distancia, mostrando primero a los más cercanos.")
        PuntoInvestigacion("Seguimiento en tiempo real: Una vez aceptado un servicio, el socio comparte su ubicación en tiempo real. El cliente ve un mapa con la posición actual del socio acercándose, similar a apps de ride-sharing.")
        PuntoInvestigacion("Rastreo periódico del socio: Cuando un socio se marca como 'disponible', la app actualiza su ubicación cada 30 segundos en la base de datos (Supabase) para que esté visible en las búsquedas cercanas.")
        PuntoInvestigacion("Cálculo de distancia y ETA: Se usa la fórmula de Haversine para calcular la distancia en kilómetros entre el socio y el cliente, estimando un tiempo de llegada aproximado.")
        PuntoInvestigacion("Solicitudes urgentes: Al hacer clic en 'PEDIR AHORA', la app captura la ubicación exacta del cliente y la adjunta a la solicitud de cita para que el socio sepa a dónde dirigirse.")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Otros casos de uso comunes en la industria:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        PuntoInvestigacion("Navegación turn-by-turn (Google Maps, Waze).")
        PuntoInvestigacion("Geofencing: activar acciones al entrar/salir de zonas geográficas.")
        PuntoInvestigacion("Tracking deportivo (correr, ciclismo) con registro de rutas.")
        PuntoInvestigacion("Servicios de entrega y logística con seguimiento de paquetes.")
    }

    // Permisos requeridos
    CardInvestigacion(
        titulo = "Permisos Requeridos",
        icono = Icons.Default.Security,
        color = Color(0xFFE65100)
    ) {
        Text(
            text = "El acceso a la ubicación en Android tiene un sistema de permisos granular con diferentes niveles de precisión:",
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        CodigoPermiso("android.permission.ACCESS_FINE_LOCATION", "Permiso peligroso (runtime). Proporciona ubicación precisa usando GPS, Wi-Fi y red celular. Precisión de ~3-5 metros. Requerido para funciones como seguimiento en tiempo real.")
        CodigoPermiso("android.permission.ACCESS_COARSE_LOCATION", "Permiso peligroso (runtime). Proporciona ubicación aproximada basada en torres celulares y Wi-Fi. Precisión de ~1-3 kilómetros. Suficiente para mostrar contenido regional.")
        CodigoPermiso("android.permission.ACCESS_BACKGROUND_LOCATION", "Permiso especial (Android 10+). Permite acceder a la ubicación cuando la app está en segundo plano. Requiere justificación adicional en Google Play.")
        CodigoPermiso("android.permission.FOREGROUND_SERVICE_LOCATION", "Requerido en Android 14+ para servicios en primer plano que acceden a ubicación.")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "En FixNow solicitamos ACCESS_FINE_LOCATION y ACCESS_COARSE_LOCATION. " +
                    "El permiso se pide en tiempo de ejecución usando rememberLauncherForActivityResult con el contrato RequestPermission " +
                    "antes de acceder al FusedLocationProviderClient.",
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ══════════════════════════════════════════════════════════════
//  COMPONENTES REUTILIZABLES
// ══════════════════════════════════════════════════════════════

@Composable
fun SeccionInvestigacion(icono: ImageVector, titulo: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            titulo,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun CardInvestigacion(
    titulo: String,
    icono: ImageVector,
    color: Color,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            contenido()
        }
    }
}

@Composable
fun PuntoInvestigacion(texto: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        crossAxisAlignment = CrossAxisAlignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(OrangePrimary)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = texto,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
        )
    }
}

@Composable
fun CodigoPermiso(permiso: String, descripcion: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = permiso,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A1B9A)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = descripcion,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

// Alias para que Row dentro de Column funcione con crossAxisAlignment
private object CrossAxisAlignment {
    val Top = Alignment.Top
}

@Composable
private fun Row(
    modifier: Modifier = Modifier,
    crossAxisAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = crossAxisAlignment,
        content = content
    )
}
