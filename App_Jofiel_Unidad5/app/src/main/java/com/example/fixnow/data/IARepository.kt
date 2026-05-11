@file:OptIn(io.github.jan.supabase.annotations.SupabaseExperimental::class)
package com.example.fixnow.data

import android.util.Log
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

object IARepository {
    private val supabase = SupabaseClient.client
    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; coerceInputValues = true }) }
        install(HttpTimeout) { requestTimeoutMillis = 60000; connectTimeoutMillis = 60000; socketTimeoutMillis = 60000 }
    }
    private const val API_KEY = "AIzaSyBibMlduR9eZqeBQdqYBPqOtXCf0ftlhSE"
    private const val GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=$API_KEY"

    internal var obtenerResenas: suspend (String) -> List<ResenaDB> = { uidLimpio ->
        supabase.postgrest["resenas"].select { filter { eq("id_socio", uidLimpio) } }.decodeList<ResenaDB>()
    }
    internal var generarResumenConGemini: suspend (String) -> String? = { prompt ->
        val response = httpClient.post(GEMINI_URL) {
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                putJsonArray("contents") { addJsonObject { put("role", "user"); putJsonArray("parts") { addJsonObject { put("text", prompt) } } } }
            })
        }
        if (response.status != HttpStatusCode.OK) null
        else {
            val rt = response.bodyAsText()
            val jr = Json.parseToJsonElement(rt).jsonObject
            jr["candidates"]?.jsonArray?.getOrNull(0)?.jsonObject?.get("content")?.jsonObject?.get("parts")?.jsonArray?.getOrNull(0)?.jsonObject?.get("text")?.jsonPrimitive?.content
        }
    }
    internal var guardarResumenSocio: suspend (String, String) -> Unit = { uidLimpio, resumenFinal ->
        supabase.postgrest["Usuarios"].update(update = { set("resumen_ia", resumenFinal) }) { filter { eq("id", uidLimpio) } }
    }

    suspend fun generarResumenSocio(socioId: String): Boolean {
        val uidLimpio = socioId.replace("\"", "").replace("'", "").trim()
        return try {
            val resenas = obtenerResenas(uidLimpio)
            if (resenas.isEmpty()) return false
            val comentariosConcat = resenas.joinToString("\n") { "- ${it.comentario}" }
            val prompt = "Genera un resumen de 3 lineas sobre este trabajador basado en estas resenas:\n$comentariosConcat"
            val resumenFinal = generarResumenConGemini(prompt).orEmpty()
            if (resumenFinal.isNotBlank()) { guardarResumenSocio(uidLimpio, resumenFinal); true } else false
        } catch (e: Exception) { false }
    }
}
