package com.yuquilema.multi_timerfood.notificaciones

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yuquilema.multi_timerfood.MainActivity
import com.yuquilema.multi_timerfood.R

/** Se ejecuta cuando el tiempo de un timer se cumple (aunque la app esté cerrada). */
class TimerFinishedWorker(
    context: android.content.Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_TIMER_ID = "timer_id"
        const val KEY_FOOD_NAME = "food_name"
        const val KEY_SOUND_ENABLED = "sound_enabled"
        const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    }

    override suspend fun doWork(): Result {
        val timerId = inputData.getInt(KEY_TIMER_ID, -1)
        val foodName = inputData.getString(KEY_FOOD_NAME) ?: "Tu comida"
        val soundEnabled = inputData.getBoolean(KEY_SOUND_ENABLED, true)
        val vibrationEnabled = inputData.getBoolean(KEY_VIBRATION_ENABLED, true)

        showNotification(timerId, foodName, soundEnabled, vibrationEnabled)
        return Result.success()
    }

    private fun showNotification(
        timerId: Int,
        foodName: String,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean,
    ) {
        // Android 13+ (API 33) requiere permiso en tiempo de ejecución para notificar.
        // Si no fue concedido, no mostramos nada (el timer se ve igual como
        // terminado dentro de la app, solo no llega el aviso del sistema).
        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val channelId = NotificationChannels.channelFor(soundEnabled, vibrationEnabled)

        val openAppIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            timerId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("¡$foodName está listo!")
            .setContentText("Tu temporizador terminó. Toca para revisarlo.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(timerId, notification)
    }
}