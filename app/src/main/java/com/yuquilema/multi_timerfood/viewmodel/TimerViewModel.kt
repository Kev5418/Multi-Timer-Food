package com.yuquilema.multi_timerfood.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yuquilema.multi_timerfood.AppDatabase
import com.yuquilema.multi_timerfood.data.ActiveTimer
import com.yuquilema.multi_timerfood.data.TimerHistoryDao
import com.yuquilema.multi_timerfood.data.TimerHistoryItem
import com.yuquilema.multi_timerfood.data.entity.Timer
import com.yuquilema.multi_timerfood.data.repository.TimerRepository
import com.yuquilema.multi_timerfood.data.toActiveTimer
import com.yuquilema.multi_timerfood.notificaciones.InAppAlarmPlayer
import com.yuquilema.multi_timerfood.notificaciones.TimerNotificationScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val historyDao: TimerHistoryDao = AppDatabase.getInstance(application).timerHistoryDao()
    private val timerRepository = TimerRepository(AppDatabase.getInstance(application).timerDao())

    // Temporizadores activos mostrados en Home. Siempre respaldados por la tabla `timers`.
    val activeTimers = mutableStateListOf<ActiveTimer>()

    // Historial persistido (leído desde Room; se refresca tras cada cambio).
    val history = mutableStateListOf<TimerHistoryItem>()

    private val tickJobs = mutableMapOf<Int, Job>()
    private val ticksSinceSave = mutableMapOf<Int, Int>()
    private val AUTOSAVE_EVERY_TICKS = 5 // guarda progreso cada ~5s mientras corre

    init {
        refreshHistory()
        restoreActiveTimersFromRoom()
    }

    private fun refreshHistory() {
        history.clear()
        history.addAll(historyDao.obtenerTodos())
    }

    /**
     * Al abrir la app, recupera de Room los timers que quedaron RUNNING/PAUSED,
     * recalcula cuánto tiempo pasó realmente y:
     * - si ya se cumplieron mientras la app estaba cerrada -> van directo al historial
     * - si no -> se restauran en Home, siguen corriendo/pausados, y se reprograma
     *   su notificación (WorkManager puede haber perdido el trabajo pendiente si
     *   el usuario forzó el cierre de la app).
     */
    private fun restoreActiveTimersFromRoom() {
        viewModelScope.launch {
            val activos = timerRepository.getActivos()
            for (entity in activos) {
                val restored = entity.toActiveTimer()
                if (restored.remainingSeconds <= 0 && entity.estado == "RUNNING") {
                    finalizeWhileClosed(entity)
                } else {
                    activeTimers.add(restored)
                    if (restored.isRunning) {
                        startTicking(restored)
                        scheduleNotification(restored)
                    }
                }
            }
        }
    }

    fun createTimer(
        foodName: String,
        category: String,
        totalSeconds: Int,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean,
    ) {
        viewModelScope.launch {
            val entity = Timer(foodName, totalSeconds, category).apply {
                estado = "RUNNING"
                tiempoInicioMillis = System.currentTimeMillis()
                segundosRestantes = totalSeconds
                isSonidoActivado = soundEnabled
                isVibracionActivada = vibrationEnabled
            }
            val newId = timerRepository.insert(entity)
            val timer = ActiveTimer(
                id = newId,
                foodName = foodName,
                category = category,
                totalSeconds = totalSeconds,
                soundEnabled = soundEnabled,
                vibrationEnabled = vibrationEnabled,
            )
            activeTimers.add(timer)
            startTicking(timer)
            scheduleNotification(timer)
        }
    }

    private fun startTicking(timer: ActiveTimer) {
        tickJobs[timer.id]?.cancel()
        ticksSinceSave[timer.id] = 0
        tickJobs[timer.id] = viewModelScope.launch {
            while (timer.isRunning && timer.remainingSeconds > 0) {
                delay(1000)
                if (timer.isRunning) {
                    timer.remainingSeconds -= 1
                    val ticks = (ticksSinceSave[timer.id] ?: 0) + 1
                    if (ticks >= AUTOSAVE_EVERY_TICKS) {
                        ticksSinceSave[timer.id] = 0
                        persistProgress(timer)
                    } else {
                        ticksSinceSave[timer.id] = ticks
                    }
                }
            }
            if (timer.remainingSeconds <= 0) {
                timer.isRunning = false
                timer.isFinished = true
                // El proceso de la app sigue vivo y ya se dio cuenta -> cancela el
                // aviso programado (evita notificación duplicada) y en su lugar
                // suena/vibra directamente, que es lo que realmente se siente
                // cuando tienes la app abierta o recién minimizada.
                TimerNotificationScheduler.cancel(appContext, timer.id)
                InAppAlarmPlayer.start(appContext, timer.soundEnabled, timer.vibrationEnabled)
                saveToHistory(timer, completed = true)
                deleteRoomRow(timer.id)
            }
        }
    }

    private fun scheduleNotification(timer: ActiveTimer) {
        TimerNotificationScheduler.schedule(
            context = appContext,
            timerId = timer.id,
            foodName = timer.foodName,
            remainingSeconds = timer.remainingSeconds,
            soundEnabled = timer.soundEnabled,
            vibrationEnabled = timer.vibrationEnabled,
        )
    }

    /** Guarda el progreso actual en Room (red de seguridad ante un cierre inesperado). */
    private fun persistProgress(timer: ActiveTimer) {
        viewModelScope.launch {
            val entity = timerRepository.getById(timer.id) ?: return@launch
            entity.estado = "RUNNING"
            entity.tiempoInicioMillis = System.currentTimeMillis()
            entity.segundosRestantes = timer.remainingSeconds
            timerRepository.update(entity)
        }
    }

    fun togglePauseResume(timer: ActiveTimer) {
        if (timer.isFinished) return
        timer.isRunning = !timer.isRunning
        viewModelScope.launch {
            val entity = timerRepository.getById(timer.id) ?: return@launch
            if (timer.isRunning) {
                entity.estado = "RUNNING"
                entity.tiempoInicioMillis = System.currentTimeMillis()
            } else {
                entity.estado = "PAUSED"
            }
            entity.segundosRestantes = timer.remainingSeconds
            timerRepository.update(entity)
        }
        if (timer.isRunning) {
            startTicking(timer)
            scheduleNotification(timer)
        } else {
            TimerNotificationScheduler.cancel(appContext, timer.id)
        }
    }

    fun restartTimer(timer: ActiveTimer) {
        tickJobs[timer.id]?.cancel()
        timer.reset()
        viewModelScope.launch {
            val entity = timerRepository.getById(timer.id) ?: return@launch
            entity.estado = "RUNNING"
            entity.tiempoInicioMillis = System.currentTimeMillis()
            entity.segundosRestantes = timer.totalSeconds
            timerRepository.update(entity)
        }
        startTicking(timer)
        scheduleNotification(timer)
    }

    fun removeTimer(timer: ActiveTimer, saveAsCompleted: Boolean = false) {
        tickJobs[timer.id]?.cancel()
        tickJobs.remove(timer.id)
        ticksSinceSave.remove(timer.id)
        activeTimers.remove(timer)
        TimerNotificationScheduler.cancel(appContext, timer.id)
        InAppAlarmPlayer.stop() // el usuario ya vio/descartó el timer -> corta la alarma
        if (saveAsCompleted) {
            saveToHistory(timer, completed = timer.isFinished)
        }
        deleteRoomRow(timer.id)
    }

    private fun deleteRoomRow(id: Int) {
        viewModelScope.launch {
            timerRepository.getById(id)?.let { timerRepository.delete(it) }
        }
    }

    private fun saveToHistory(timer: ActiveTimer, completed: Boolean) {
        val item = TimerHistoryItem()
        item.foodName = timer.foodName
        item.category = timer.category
        item.totalSeconds = timer.totalSeconds
        item.isCompleted = completed
        item.dateMillis = System.currentTimeMillis()
        historyDao.insertar(item)
        refreshHistory()
    }

    /** Timer que se cumplió mientras la app estaba cerrada: va directo al historial. */
    private fun finalizeWhileClosed(entity: Timer) {
        val item = TimerHistoryItem()
        item.foodName = entity.nombreAlimento
        item.category = entity.categoria
        item.totalSeconds = entity.duracionSegundos
        item.isCompleted = true
        item.dateMillis = System.currentTimeMillis()
        historyDao.insertar(item)
        refreshHistory()
        deleteRoomRow(entity.id)
    }

    fun deleteHistoryItem(item: TimerHistoryItem) {
        historyDao.eliminar(item)
        refreshHistory()
    }

    fun clearHistory() {
        historyDao.eliminarTodos()
        refreshHistory()
    }

    override fun onCleared() {
        super.onCleared()
        tickJobs.values.forEach { it.cancel() }
        InAppAlarmPlayer.stop()
    }
}