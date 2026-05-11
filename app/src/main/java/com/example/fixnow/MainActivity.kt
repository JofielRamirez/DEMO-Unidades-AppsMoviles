package com.example.fixnow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fixnow.data.SupabaseClient
import com.example.fixnow.screens.*
import com.example.fixnow.ui.theme.FixNowTheme
import com.example.fixnow.utils.NotificationHelper
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.status.SessionStatus

// Estado global del tema
object TemaApp {
    var oscuro by mutableStateOf<Boolean?>(null)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        SupabaseClient.client.handleDeeplinks(intent)
        NotificationHelper.crearCanales(this)

        setContent {
            val sistemaOscuro = isSystemInDarkTheme()
            val usarOscuro = TemaApp.oscuro ?: sistemaOscuro

            FixNowTheme(darkTheme = usarOscuro) {
                AppNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        SupabaseClient.client.handleDeeplinks(intent)
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sessionStatus by SupabaseClient.client.auth.sessionStatus.collectAsState()

    // ══════════════════════════════════════════════════════════════
    //  NAVEGACIÓN PRINCIPAL — Menú de Prácticas + App FixNow
    // ══════════════════════════════════════════════════════════════

    NavHost(navController = navController, startDestination = "menu_practicas") {

        // ── MENÚ DE PRÁCTICAS (Pantalla inicial) ─────────────────
        composable("menu_practicas") { PantallaMenuPracticas(navController) }

        // ── PRÁCTICA 5.1 — Investigación de Sensores ─────────────
        composable("practica_5_1") { PantallaPractica51(navController) }

        // ── PRÁCTICA 5.2 — Implementación de Sensores ────────────
        composable("practica_5_2") { PantallaPractica52(navController) }

        // ── PRÁCTICA 5.3 — Proyecto Final (FixNow) ───────────────
        composable("practica_5_3_entry") { PantallaPractica53Entry(navController) }

        // ══════════════════════════════════════════════════════════
        //  RUTAS DE FIXNOW (Proyecto Final completo)
        // ══════════════════════════════════════════════════════════
        composable("login")    { PantallaLogin(navController) }
        composable("registro") { PantallaRegistro(navController) }
        composable("inicio")   { PantallaInicio(navController) }
        composable("servicios") { PantallaServicios(navController) }
        composable("perfil")   { PantallaPerfil(navController) }
        composable("mensajes") { PantallaListaChats(navController) }
        composable("registro_socio") { PantallaRegistroSocio(navController) }

        // Rutas de Socio
        composable("socio_citas") { PantallaSocioCitas(navController) }
        composable("socio_historial") { PantallaSocioHistorial(navController) }
        composable("socio_resenas") { PantallaSocioResenas(navController) }
        composable("socio_perfil") { PantallaSocioPerfil(navController) }

        composable("detalle_socio/{socioId}") { backStackEntry ->
            PantallaDetalleSocio(navController, backStackEntry.arguments?.getString("socioId") ?: "")
        }
        composable("seguimiento/{citaId}") { backStackEntry ->
            PantallaSeguimientoSocio(navController, backStackEntry.arguments?.getString("citaId") ?: "")
        }
        composable("chat/{socioId}/{nombre}") { backStackEntry ->
            PantallaChat(
                navController,
                backStackEntry.arguments?.getString("socioId") ?: "",
                backStackEntry.arguments?.getString("nombre") ?: "Socio"
            )
        }
        composable("servicios/{categoria}") { backStackEntry ->
            PantallaListaServicios(navController, backStackEntry.arguments?.getString("categoria") ?: "Servicio")
        }
        composable("testing") { PantallaTesting(navController) }
    }
}
