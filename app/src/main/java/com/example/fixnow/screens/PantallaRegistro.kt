package com.example.fixnow.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fixnow.ui.theme.*
import com.example.fixnow.data.SupabaseClient
import com.example.fixnow.data.UsuarioRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

@Composable
fun PantallaRegistro(navController: NavController) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.35f).background(brush = Brush.verticalGradient(colors = listOf(OrangeDark, OrangePrimary))))
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.65f).align(Alignment.BottomCenter).background(Color(0xFFF5F5F5)))
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedVisibility(visible = visible, enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 52.dp, bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("FixNow", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Crea tu cuenta gratis", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
            AnimatedVisibility(visible = visible, enter = fadeIn() + slideInVertically(initialOffsetY = { 80 })) {
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(16.dp)) {
                    Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Registro", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                        Spacer(modifier = Modifier.height(24.dp))
                        CampoTextoIcono(value = nombre, onValueChange = { nombre = it }, placeholder = "Nombre completo", leadingIcon = { Icon(Icons.Default.Person, null, tint = OrangePrimary, modifier = Modifier.size(20.dp)) })
                        Spacer(modifier = Modifier.height(14.dp))
                        CampoTextoIcono(value = email, onValueChange = { email = it }, placeholder = "Correo electronico", leadingIcon = { Icon(Icons.Default.Email, null, tint = OrangePrimary, modifier = Modifier.size(20.dp)) })
                        Spacer(modifier = Modifier.height(14.dp))
                        CampoTextoIcono(value = password, onValueChange = { password = it }, placeholder = "Contrasena (min. 8 caracteres)", esPassword = !passwordVisible, leadingIcon = { Icon(Icons.Default.Lock, null, tint = OrangePrimary, modifier = Modifier.size(20.dp)) }, trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(20.dp)) } })
                        if (mensajeError.isNotEmpty()) { Spacer(Modifier.height(10.dp)); Text(mensajeError, color = ErrorRed, fontSize = 12.sp) }
                        if (mensajeExito.isNotEmpty()) { Spacer(Modifier.height(10.dp)); Text(mensajeExito, color = ColorSuccess, fontSize = 12.sp) }
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = {
                            when {
                                nombre.isBlank() || email.isBlank() || password.isBlank() -> mensajeError = "Completa todos los campos"
                                password.length < 8 -> mensajeError = "La contrasena debe tener al menos 8 caracteres"
                                else -> { mensajeError = ""; cargando = true
                                    scope.launch {
                                        try {
                                            SupabaseClient.client.auth.signUpWith(Email) { this.email = email.trim().lowercase(); this.password = password }
                                            val uid = SupabaseClient.client.auth.currentUserOrNull()?.id
                                            if (uid != null) { try { UsuarioRepository.guardarUsuario(uid, email.trim().lowercase(), nombre.trim()) } catch (_: Exception) {} }
                                            else { mensajeExito = "Revisa tu correo para confirmar tu cuenta!"; cargando = false }
                                        } catch (e: Exception) { mensajeError = "Error: ${e.message}"; cargando = false }
                                    }
                                }
                            }
                        }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary), enabled = !cargando) {
                            if (cargando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp)) else Text("Crear Cuenta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            Text("Ya tienes cuenta? ", fontSize = 13.sp, color = Color(0xFF9E9E9E))
                            Text("Inicia sesion", fontSize = 13.sp, color = OrangePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
