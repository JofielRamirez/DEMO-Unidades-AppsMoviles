package com.example.fixnow.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fixnow.ui.theme.*

@Composable
fun PantallaMenuPracticas(navController: NavController) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.38f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(OrangeDark, OrangePrimary)
                    )
                )
        )

        // Fondo inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── HEADER ────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 56.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Unidad 5",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Prácticas del Semestre — FixNow",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── TARJETAS DE PRÁCTICAS ─────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 80 })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CardPractica(
                        numero = "5.1",
                        titulo = "Investigación de Sensores",
                        descripcion = "Investigación sobre los sensores de Cámara y GPS: funcionamiento, casos de uso y permisos requeridos.",
                        icono = Icons.Default.MenuBook,
                        colorIcono = Color(0xFF1976D2),
                        onClick = { navController.navigate("practica_5_1") }
                    )

                    CardPractica(
                        numero = "5.2",
                        titulo = "Implementación de Sensores",
                        descripcion = "App que utiliza los sensores de Cámara y GPS del dispositivo con implementación funcional.",
                        icono = Icons.Default.Sensors,
                        colorIcono = Color(0xFF388E3C),
                        onClick = { navController.navigate("practica_5_2") }
                    )

                    CardPractica(
                        numero = "5.3",
                        titulo = "Proyecto Final — FixNow",
                        descripcion = "Aplicación completa de servicios a domicilio con chat en tiempo real, citas, GPS y más.",
                        icono = Icons.Default.RocketLaunch,
                        colorIcono = OrangePrimary,
                        onClick = { navController.navigate("practica_5_3_entry") }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Footer
                    Text(
                        text = "Programación para Móviles\nSemestre 2026-1",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun CardPractica(
    numero: String,
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    colorIcono: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorIcono.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, null, tint = colorIcono, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = colorIcono.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "Práctica $numero",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorIcono
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    descripcion,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.ChevronRight,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    }
}
