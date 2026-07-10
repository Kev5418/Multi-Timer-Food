package com.yuquilema.multi_timerfood.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yuquilema.multi_timerfood.data.entity.Timer
import java.util.Locale

/**
 * Temporizador activo (corriendo/pausado) que se muestra en Home.
 * Es un espejo en memoria de una fila de la tabla `timers` (Room);
 * el id siempre coincide con el id autogenerado por Room para esa fila.
 */
class ActiveTimer(
    val id: Int,
    val foodName: String,
    val category: String,
    val totalSeconds: Int,
    val soundEnabled: Boolean,
    val vibrationEnabled: Boolean,
    initialRemainingSeconds: Int = totalSeconds,
    initialIsRunning: Boolean = true,
) {
    var remainingSeconds by mutableIntStateOf(initialRemainingSeconds)
    var isRunning by mutableStateOf(initialIsRunning)
    var isFinished by mutableStateOf(initialRemainingSeconds <= 0)

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

/**
 * Convierte una fila de Room en el modelo en memoria usado por la UI,
 * recalculando el tiempo restante si el timer seguía "RUNNING" mientras
 * la app estaba cerrada (para que no se "congele" el tiempo).
 */
fun Timer.toActiveTimer(now: Long = System.currentTimeMillis()): ActiveTimer {
    var remaining = segundosRestantes
    val wasRunning = estado == "RUNNING"
    if (wasRunning) {
        val elapsedSeconds = ((now - tiempoInicioMillis) / 1000L).toInt()
        remaining = (segundosRestantes - elapsedSeconds).coerceAtLeast(0)
    }
    return ActiveTimer(
        id = id,
        foodName = nombreAlimento ?: "",
        category = categoria ?: "",
        totalSeconds = duracionSegundos,
        soundEnabled = isSonidoActivado,
        vibrationEnabled = isVibracionActivada,
        initialRemainingSeconds = remaining,
        initialIsRunning = wasRunning && remaining > 0,
    )
}