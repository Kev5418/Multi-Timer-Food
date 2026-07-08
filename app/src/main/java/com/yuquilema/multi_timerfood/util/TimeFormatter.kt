package com.yuquilema.multi_timerfood.util

import java.util.Locale

/**
 * Formateo de tiempos compartido por las pantallas y adaptadores de la app.
 * Centraliza la conversión de segundos a texto para evitar duplicar la lógica.
 */
object TimeFormatter {

    /** Formatea segundos como `m:ss` (p. ej. 90 -> "1:30"). */
    fun minutesSeconds(totalSeconds: Int): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", m, s)
    }

    /** Formatea segundos como `hh:mm:ss` (p. ej. 3661 -> "01:01:01"). */
    fun hoursMinutesSeconds(totalSeconds: Int): String {
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    }
}
