package com.yuquilema.multi_timerfood.data.repository

import androidx.lifecycle.LiveData
import com.yuquilema.multi_timerfood.data.dao.TimerDao
import com.yuquilema.multi_timerfood.data.entity.Timer

class TimerRepository(private val timerDao: TimerDao) {

    val allTimers: LiveData<List<Timer>> = timerDao.getAll()

    // Devuelve el id autogenerado por Room (antes no devolvía nada).
    suspend fun insert(timer: Timer): Int = timerDao.insert(timer).toInt()

    suspend fun update(timer: Timer) = timerDao.update(timer)
    suspend fun delete(timer: Timer) = timerDao.delete(timer)
    suspend fun getById(id: Int): Timer? = timerDao.getById(id)

    // NUEVO: timers que estaban RUNNING o PAUSED cuando se cerró la app.
    suspend fun getActivos(): List<Timer> = timerDao.getActivos()

    suspend fun finalizarTimer(id: Int) {
        val timer = timerDao.getById(id) ?: return
        timer.estado = "FINISHED"
        timerDao.update(timer)
    }
}
