package com.yuquilema.multi_timerfood.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
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

    private val tickJobs = mutableMapOf<String, Job>()

    init {
        refreshHistory()
    }

    private fun refreshHistory() {
        history.clear()
        history.addAll(dao.obtenerTodos())
    }

    fun createTimer(
        foodName: String,
        category: String,
        totalSeconds: Int,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ) {
        val timer = ActiveTimer(
            foodName = foodName,
            category = category,
            totalSeconds = totalSeconds,
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled
        )
        activeTimers.add(timer)
        startTicking(timer)
    }

    private fun startTicking(timer: ActiveTimer) {
        tickJobs[timer.id]?.cancel()
        tickJobs[timer.id] = viewModelScope.launch {
            while (timer.isRunning && timer.remainingSeconds > 0) {
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
        dao.insertar(item)
        refreshHistory()
    }

    fun deleteHistoryItem(item: TimerHistoryItem) {
        dao.eliminar(item)
        refreshHistory()
    }

    fun clearHistory() {
        dao.eliminarTodos()
        refreshHistory()
    }

    override fun onCleared() {
        super.onCleared()
        tickJobs.values.forEach { it.cancel() }
    }
}
