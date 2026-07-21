package com.yuquilema.multi_timerfood.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yuquilema.multi_timerfood.data.dao.TimerDao
import com.yuquilema.multi_timerfood.data.entity.Timer
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [TimerRepository], exercising delegation to the DAO and the
 * [TimerRepository.finalizarTimer] state transition against an in-memory fake DAO.
 */
class TimerRepositoryTest {

    /** Minimal in-memory [TimerDao] that records interactions. */
    private class FakeTimerDao : TimerDao {
        val stored = linkedMapOf<Int, Timer>()
        val deleted = mutableListOf<Timer>()
        private val live = MutableLiveData<List<Timer>>()

        override fun insert(timer: Timer) {
            stored[timer.id] = timer
        }

        override fun update(timer: Timer) {
            stored[timer.id] = timer
        }

        override fun delete(timer: Timer) {
            stored.remove(timer.id)
            deleted.add(timer)
        }

        override fun getAll(): LiveData<List<Timer>> = live

        override fun getById(timerId: Int): Timer? = stored[timerId]
    }

    private lateinit var dao: FakeTimerDao
    private lateinit var repository: TimerRepository

    @Before
    fun setUp() {
        dao = FakeTimerDao()
        repository = TimerRepository(dao)
    }

    @Test
    fun `allTimers exposes the dao live data`() {
        assertSame(dao.getAll(), repository.allTimers)
    }

    @Test
    fun `insert delegates to dao`() = runBlocking {
        val timer = Timer(1, "Steak", 300, "Meat", "IDLE", null, "Bell")
        repository.insert(timer)
        assertSame(timer, dao.stored[1])
    }

    @Test
    fun `getById returns stored timer`() = runBlocking {
        val timer = Timer(3, "Egg", 180, "Eggs", "IDLE", null, "Chime")
        dao.stored[3] = timer
        assertSame(timer, repository.getById(3))
    }

    @Test
    fun `getById returns null when absent`() = runBlocking {
        assertNull(repository.getById(99))
    }

    @Test
    fun `delete removes timer via dao`() = runBlocking {
        val timer = Timer(2, "Rice", 600, "Pasta", "IDLE", null, "Bell")
        dao.stored[2] = timer
        repository.delete(timer)
        assertFalse(dao.stored.containsKey(2))
        assertTrue(dao.deleted.contains(timer))
    }

    @Test
    fun `finalizarTimer marks the timer as FINISHED`() = runBlocking {
        val timer = Timer(4, "Cake", 1200, "Desserts", "RUNNING", null, "Bell")
        dao.stored[4] = timer
        repository.finalizarTimer(4)
        assertEquals("FINISHED", dao.stored[4]!!.estado)
    }

    @Test
    fun `finalizarTimer is a no-op for unknown id`() = runBlocking {
        repository.finalizarTimer(123)
        assertTrue(dao.stored.isEmpty())
    }
}
