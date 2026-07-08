package com.yuquilema.multi_timerfood.ui.timer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yuquilema.multi_timerfood.AppDatabase
import com.yuquilema.multi_timerfood.data.entity.Timer
import com.yuquilema.multi_timerfood.data.repository.TimerRepository
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TimerRepository
    val allTimers: LiveData<List<Timer>>
    private val appContext = application.applicationContext

    // Errores de persistencia expuestos para que la Activity los muestre en vez
    // de que la excepción tumbe la app dentro de la corrutina.
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        val dao = AppDatabase.getInstance(application).timerDao()
        repository = TimerRepository(dao)
        allTimers = repository.allTimers
    }

    /** La UI llama esto tras mostrar el error para no repetirlo. */
    fun clearError() {
        _error.value = null
    }

    fun getById(id: Int, callback: (Timer?) -> Unit) = viewModelScope.launch {
        val timer = try {
            repository.getById(id)
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo cargar el temporizador $id", e)
            _error.value = "No se pudo cargar el temporizador"
            // Invocamos el callback igual para que quien llama no quede colgado.
            callback(null)
            return@launch
        }
        callback(timer)
    }

    fun insert(timer: Timer) = runOnDb("No se pudo guardar el temporizador") {
        repository.insert(timer)
    }

    fun update(timer: Timer) = runOnDb("No se pudo actualizar el temporizador") {
        repository.update(timer)
    }

    fun delete(timer: Timer) = runOnDb("No se pudo eliminar el temporizador") {
        // if (timer.estado == "RUNNING") AlarmScheduler.cancelar(appContext, timer.id, timer.sonidoNotificacion)
        repository.delete(timer)
    }

    // NUEVO: iniciar
    fun iniciarTimer(timer: Timer) = runOnDb("No se pudo iniciar el temporizador") {
        timer.estado = "RUNNING"
        timer.tiempoInicioMillis = System.currentTimeMillis()
        repository.update(timer)
        // AlarmScheduler.programar(appContext, timer, timer.duracionSegundos, timer.sonidoNotificacion)
    }

    // NUEVO: pausar
    fun pausarTimer(timer: Timer) = runOnDb("No se pudo pausar el temporizador") {
        // AlarmScheduler.cancelar(appContext, timer.id, timer.sonidoNotificacion)
        timer.estado = "PAUSED"
        repository.update(timer)
    }

    // NUEVO: reanudar, calculando lo que falta
    fun reanudarTimer(timer: Timer) = runOnDb("No se pudo reanudar el temporizador") {
        val transcurridoSeg = ((System.currentTimeMillis() - timer.tiempoInicioMillis) / 1000).toInt()
        val restante = (timer.duracionSegundos - transcurridoSeg).coerceAtLeast(0)
        timer.estado = "RUNNING"
        timer.tiempoInicioMillis = System.currentTimeMillis()
        repository.update(timer)
        // AlarmScheduler.programar(appContext, timer, restante, timer.sonidoNotificacion)
    }

    // NUEVO: cambiar sonido elegido en el bottom sheet
    fun actualizarSonido(timer: Timer, sonido: String) = runOnDb("No se pudo actualizar el sonido") {
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

    /**
     * Ejecuta una operación de base de datos en la corrutina de la ViewModel
     * capturando cualquier excepción para que no se propague sin control y
     * tumbe la app; en su lugar la reporta a través de [error].
     */
    private fun runOnDb(errorMsg: String, block: suspend () -> Unit) =
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                Log.e(TAG, errorMsg, e)
                _error.value = errorMsg
            }
        }

    companion object {
        private const val TAG = "TimerViewModel"
    }
}
