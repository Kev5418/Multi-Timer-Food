package com.yuquilema.multi_timerfood.data.repository

import androidx.lifecycle.LiveData
import com.yuquilema.multi_timerfood.data.dao.TimerDao
import com.yuquilema.multi_timerfood.data.entity.Timer

class TimerRepository(private val timerDao: TimerDao) {

    val allTimers: LiveData<List<Timer>> = timerDao.getAll()

    suspend fun insert(timer: Timer) = timerDao.insert(timer)
    suspend fun update(timer: Timer) = timerDao.update(timer)
    suspend fun delete(timer: Timer) = timerDao.delete(timer)
}
