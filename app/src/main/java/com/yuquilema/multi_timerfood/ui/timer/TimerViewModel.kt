package com.yuquilema.multi_timerfood.ui.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.yuquilema.multi_timerfood.AppDatabase
import com.yuquilema.multi_timerfood.data.entity.Timer
import com.yuquilema.multi_timerfood.data.repository.TimerRepository
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TimerRepository
    val allTimers: LiveData<List<Timer>>
    private val appContext = application.applicationContext

    init {
        val dao = AppDatabase.getInstance(application).timerDao()
        repository = TimerRepository(dao)
        allTimers = repository.allTimers
    }

    fun getById(id: Int, callback: (Timer?) -> Unit) = viewModelScope.launch {
        val timer = repository.getById(id)
        callback(timer)
    }

    fun insert(timer: Timer) = viewModelScope.launch { repository.insert(timer) }
    fun update(timer: Timer) = viewModelScope.launch { repository.update(timer) }
    
    fun delete(timer: Timer) = viewModelScope.launch {
        // if (timer.estado == "RUNNING") AlarmScheduler.cancelar(appContext, timer.id, timer.sonidoNotificacion)
        repository.delete(timer)
    }

    // NUEVO: iniciar
    fun iniciarTimer(timer: Timer) = viewModelScope.launch {
        timer.estado = "RUNNING"
        timer.tiempoInicioMillis = System.currentTimeMillis()
        repository.update(timer)
        // AlarmScheduler.programar(appContext, timer, timer.duracionSegundos, timer.sonidoNotificacion)
    }

    // NUEVO: pausar
    fun pausarTimer(timer: Timer) = viewModelScope.launch {
        // AlarmScheduler.cancelar(appContext, timer.id, timer.sonidoNotificacion)
        timer.estado = "PAUSED"
        repository.update(timer)
    }

    // NUEVO: reanudar, calculando lo que falta
    fun reanudarTimer(timer: Timer) = viewModelScope.launch {
        val transcurridoSeg = ((System.currentTimeMillis() - timer.tiempoInicioMillis) / 1000).toInt()
        val restante = (timer.duracionSegundos - transcurridoSeg).coerceAtLeast(0)
        timer.estado = "RUNNING"
        timer.tiempoInicioMillis = System.currentTimeMillis()
        repository.update(timer)
        // AlarmScheduler.programar(appContext, timer, restante, timer.sonidoNotificacion)
    }

    // NUEVO: cambiar sonido elegido en el bottom sheet
    fun actualizarSonido(timer: Timer, sonido: String) = viewModelScope.launch {
        timer.sonidoNotificacion = sonido
        repository.update(timer)
        // Si está corriendo, hay que reprogramar la alarma en el canal nuevo
        if (timer.estado == "RUNNING") {
            // AlarmScheduler.cancelar(appContext, timer.id, timer.sonidoNotificacion)
            val transcurridoSeg = ((System.currentTimeMillis() - timer.tiempoInicioMillis) / 1000).toInt()
            val restante = (timer.duracionSegundos - transcurridoSeg).coerceAtLeast(0)
            // AlarmScheduler.programar(appContext, timer, restante, sonido)
        }
    }
}
