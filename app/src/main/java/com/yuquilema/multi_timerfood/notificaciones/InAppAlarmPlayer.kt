package com.yuquilema.multi_timerfood.notificaciones

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Alarma que suena/vibra DENTRO de la app cuando un timer llega a cero mientras
 * la app sigue con su proceso vivo (foreground o recién puesta en background).
 *
 * Es independiente de TimerFinishedWorker: la notificación del sistema es el
 * respaldo para cuando Android ya mató el proceso de la app; esto es lo que
 * se escucha/siente cuando el proceso sigue vivo y por eso ya la cancelamos.
 */
object InAppAlarmPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    fun start(context: Context, soundEnabled: Boolean, vibrationEnabled: Boolean) {
        stop() // por si ya había otra alarma sonando de otro timer

        if (soundEnabled) {
            try {
                val alarmUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    setDataSource(context, alarmUri)
                    isLooping = true
                    prepare()
                    start()
                }
            } catch (e: Exception) {
                // Si el dispositivo no tiene un tono de alarma configurado, simplemente
                // no suena; no debe tumbar la app por esto.
                mediaPlayer = null
            }
        }

        if (vibrationEnabled) {
            val vib = getVibrator(context)
            vibrator = vib
            val pattern = longArrayOf(0, 500, 250, 500, 250, 500, 250, 500)
            if (Build.VERSION.SDK_INT >= 26) {
                vib.vibrate(VibrationEffect.createWaveform(pattern, 0)) // repeat=0 -> en bucle
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(pattern, 0)
            }
        }
    }

    /** Detiene sonido y vibración. Llamar cuando el usuario descarta/acepta el timer. */
    fun stop() {
        mediaPlayer?.let {
            try { it.stop() } catch (_: Exception) { /* ya estaba detenido */ }
            it.release()
        }
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
    }

    private fun getVibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= 31) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
}