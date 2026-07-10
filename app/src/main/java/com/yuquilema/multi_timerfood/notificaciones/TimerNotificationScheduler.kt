package com.yuquilema.multi_timerfood.notificaciones

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yuquilema.multi_timerfood.notificaciones.TimerFinishedWorker
import java.util.concurrent.TimeUnit

/**
 * Programa el aviso de "timer terminado" para que WorkManager lo dispare
 * exactamente cuando falten 0 segundos, sin importar si la app sigue
 * abierta, está en segundo plano, o el proceso fue matado por el sistema.
 */
object TimerNotificationScheduler {

    private fun uniqueName(timerId: Int) = "timer_finished_$timerId"

    fun schedule(
        context: Context,
        timerId: Int,
        foodName: String,
        remainingSeconds: Int,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean,
    ) {
        if (remainingSeconds <= 0) return

        val data = Data.Builder()
            .putInt(TimerFinishedWorker.KEY_TIMER_ID, timerId)
            .putString(TimerFinishedWorker.KEY_FOOD_NAME, foodName)
            .putBoolean(TimerFinishedWorker.KEY_SOUND_ENABLED, soundEnabled)
            .putBoolean(TimerFinishedWorker.KEY_VIBRATION_ENABLED, vibrationEnabled)
            .build()

        val request = OneTimeWorkRequestBuilder<TimerFinishedWorker>()
            .setInitialDelay(remainingSeconds.toLong(), TimeUnit.SECONDS)
            .setInputData(data)
            .build()

        // REPLACE: si ya había un aviso programado para este timer (por ejemplo,
        // se pausó y se reanudó), lo reemplaza en vez de duplicarlo.
        WorkManager.getInstance(context)
            .enqueueUniqueWork(uniqueName(timerId), ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context, timerId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueName(timerId))
    }
}