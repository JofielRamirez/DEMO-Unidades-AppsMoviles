package com.example.fixnow.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.fixnow.MainActivity

object NotificationHelper {
    const val CHANNEL_ID = "fixnow_notifications"
    const val SERVICE_CHANNEL_ID = "fixnow_service_channel"

    fun crearCanales(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Notificaciones de FixNow", NotificationManager.IMPORTANCE_HIGH)
            val serviceChannel = NotificationChannel(SERVICE_CHANNEL_ID, "Estado de Conexion", NotificationManager.IMPORTANCE_LOW)
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
            nm.createNotificationChannel(serviceChannel)
        }
    }
}
