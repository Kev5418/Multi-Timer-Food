package com.yuquilema.multi_timerfood.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yuquilema.multi_timerfood.AppDatabase
import com.yuquilema.multi_timerfood.data.ActiveTimer
import com.yuquilema.multi_timerfood.data.TimerHistoryDao
import com.yuquilema.multi_timerfood.data.TimerHistoryItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: TimerHistoryDao = AppDatabase.getInstance(application).timerHistoryDao()

    // Temporizadores activos mostrados en Home.
    val activeTimers = mutableStateListOf<ActiveTimer>()

    // Historial persistido (leído desde Room; se refresca tras cada cambio).
    val history = mutableStateListOf<TimerHistoryItem>()

    // Último error de persistencia, expuesto para que la UI pueda mostrarlo.
    // Es null cuando no hay error pendiente.
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val tickJobs = mutableMapOf<String, Job>()

    init {
        refreshHistory()
    }

    /** La UI llama esto una vez que mostró el error, para no repetirlo. */
    fun consumeError() {
        errorMessage = null
    }

    private fun refreshHistory() {
        // Cargamos primero en una lista local: si la consulta falla no dejamos
        // el historial en blanco por haber limpiado antes de leer.
        val items = try {
            dao.obtenerTodos()
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo cargar el historial", e)
            errorMessage = "No se pudo cargar el historial"
            return
        }
        history.clear()
        history.addAll(items)
    }

    fun createTimer(
        foodName: String,
        category: String,
        totalSeconds: Int,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean,
    ) {
        val timer = ActiveTimer(
            foodName = foodName,
            category = category,
            totalSeconds = totalSeconds,
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled,
        )
        activeTimers.add(timer)
        startTicking(timer)
    }

    private fun startTicking(timer: ActiveTimer) {
        tickJobs[timer.id]?.cancel()
        tickJobs[timer.id] = viewModelScope.launch {
            while (timer.isRunning && (timer.remainingSeconds > 0)) {
                delay(1000)
                if (timer.isRunning) {
                    timer.remainingSeconds -= 1
                }
            }
            if (timer.remainingSeconds <= 0) {
                timer.isRunning = false
                timer.isFinished = true
                saveToHistory(timer, completed = true)
            }
        }
    }

    fun togglePauseResume(timer: ActiveTimer) {
        if (timer.isFinished) return
        timer.isRunning = !timer.isRunning
        if (timer.isRunning) startTicking(timer)
    }

    fun restartTimer(timer: ActiveTimer) {
        tickJobs[timer.id]?.cancel()
        timer.reset()
        startTicking(timer)
    }

    fun removeTimer(timer: ActiveTimer, saveAsCompleted: Boolean = false) {
        tickJobs[timer.id]?.cancel()
        tickJobs.remove(timer.id)
        activeTimers.remove(timer)
        if (saveAsCompleted) {
            saveToHistory(timer, completed = timer.isFinished)
        }
    }

    private fun saveToHistory(timer: ActiveTimer, completed: Boolean) {
        val item = TimerHistoryItem()
        item.foodName = timer.foodName
        item.category = timer.category
        item.totalSeconds = timer.totalSeconds
        item.isCompleted = completed
        item.dateMillis = System.currentTimeMillis()
        try {
            dao.insertar(item)
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo guardar en el historial", e)
            errorMessage = "No se pudo guardar en el historial"
            return
        }
        refreshHistory()
    }

    fun deleteHistoryItem(item: TimerHistoryItem) {
        try {
            dao.eliminar(item)
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo eliminar el elemento del historial", e)
            errorMessage = "No se pudo eliminar el elemento"
            return
        }
        refreshHistory()
    }

    fun clearHistory() {
        try {
            dao.eliminarTodos()
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo borrar el historial", e)
            errorMessage = "No se pudo borrar el historial"
            return
        }
        refreshHistory()
    }

    override fun onCleared() {
        super.onCleared()
        tickJobs.values.forEach { it.cancel() }
    }

    companion object {
        private const val TAG = "TimerViewModel"
    }
}
