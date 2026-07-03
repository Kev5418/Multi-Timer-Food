package com.yuquilema.multi_timerfood.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale
import java.util.UUID

/**
 * Temporizador activo (corriendo/pausado) que se muestra en la pantalla Home.
 * Vive en memoria mientras la app está abierta; cuando termina (o se elimina
 * manualmente) se guarda en timer_history mediante TimerHistoryDao.
 */
class ActiveTimer(
    val id: String = UUID.randomUUID().toString(),
    val foodName: String,
    val category: String,
    val totalSeconds: Int,
    val soundEnabled: Boolean,
    val vibrationEnabled: Boolean,
) {
    var remainingSeconds by mutableIntStateOf(totalSeconds)
    var isRunning by mutableStateOf(true)
    var isFinished by mutableStateOf(false)

    val progress: Float
        get() = if (totalSeconds == 0) 0f else remainingSeconds / totalSeconds.toFloat()

    fun formattedTime(): String {
        val m = remainingSeconds / 60
        val s = remainingSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", m, s)
    }

    fun reset() {
        remainingSeconds = totalSeconds
        isRunning = true
        isFinished = false
    }
}
