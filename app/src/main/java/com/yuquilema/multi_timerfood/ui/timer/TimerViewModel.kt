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

    init {
        val dao = AppDatabase.getInstance(application).timerDao()
        repository = TimerRepository(dao)
        allTimers = repository.allTimers
    }

    fun insert(timer: Timer) = viewModelScope.launch {
        repository.insert(timer)
    }

    fun update(timer: Timer) = viewModelScope.launch {
        repository.update(timer)
    }

    fun delete(timer: Timer) = viewModelScope.launch {
        repository.delete(timer)
    }
}
