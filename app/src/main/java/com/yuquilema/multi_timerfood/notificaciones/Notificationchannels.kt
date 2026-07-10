package com.yuquilema.multi_timerfood.notificaciones


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build

/**
 * En Android 8+ (API 26+) el sonido y la vibración de un canal quedan fijos
 * al momento de crearlo y no se pueden cambiar después. Por eso, en vez de
 * un solo canal "dinámico", creamos un canal por cada combinación posible
 * de sonido/vibración que puede tener un timer.
 */
object NotificationChannels {

    const val CHANNEL_SOUND_VIBRATION = "timer_sound_vibration"
    const val CHANNEL_SOUND_ONLY = "timer_sound_only"
    const val CHANNEL_VIBRATION_ONLY = "timer_vibration_only"
    const val CHANNEL_SILENT = "timer_silent"

    /** Elige el canal correcto según las preferencias del timer. */
    fun channelFor(soundEnabled: Boolean, vibrationEnabled: Boolean): String = when {
        soundEnabled && vibrationEnabled -> CHANNEL_SOUND_VIBRATION
        soundEnabled && !vibrationEnabled -> CHANNEL_SOUND_ONLY
        !soundEnabled && vibrationEnabled -> CHANNEL_VIBRATION_ONLY
        else -> CHANNEL_SILENT
    }

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val channels = listOf(
            NotificationChannel(
                CHANNEL_SOUND_VIBRATION,
                "Temporizador (sonido y vibración)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Aviso cuando un temporizador termina, con sonido y vibración"
                setSound(alarmSound, audioAttributes)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            },
            NotificationChannel(
                CHANNEL_SOUND_ONLY,
                "Temporizador (solo sonido)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Aviso cuando un temporizador termina, solo con sonido"
                setSound(alarmSound, audioAttributes)
                enableVibration(false)
            },
            NotificationChannel(
                CHANNEL_VIBRATION_ONLY,
                "Temporizador (solo vibración)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Aviso cuando un temporizador termina, solo con vibración"
                setSound(null, null)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            },
            NotificationChannel(
                CHANNEL_SILENT,
                "Temporizador (silencioso)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Aviso visual cuando un temporizador termina, sin sonido ni vibración"
                setSound(null, null)
                enableVibration(false)
            },
        )

        manager.createNotificationChannels(channels)
    }
}
