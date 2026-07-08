package com.yuquilema.multi_timerfood.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [ActiveTimer], covering its derived state ([ActiveTimer.progress],
 * [ActiveTimer.formattedTime]) and mutation helpers ([ActiveTimer.reset]).
 */
class ActiveTimerTest {

    private fun timer(totalSeconds: Int = 120) = ActiveTimer(
        foodName = "Egg",
        category = "Eggs",
        totalSeconds = totalSeconds,
        soundEnabled = true,
        vibrationEnabled = false,
    )

    @Test
    fun `new timer starts running and not finished`() {
        val t = timer(90)
        assertEquals(90, t.remainingSeconds)
        assertTrue(t.isRunning)
        assertFalse(t.isFinished)
    }

    @Test
    fun `each timer gets a unique id`() {
        assertNotEquals(timer().id, timer().id)
    }

    @Test
    fun `progress is full at start`() {
        val t = timer(60)
        assertEquals(1f, t.progress, 0.0001f)
    }

    @Test
    fun `progress reflects remaining fraction`() {
        val t = timer(100)
        t.remainingSeconds = 25
        assertEquals(0.25f, t.progress, 0.0001f)
    }

    @Test
    fun `progress is zero when remaining reaches zero`() {
        val t = timer(30)
        t.remainingSeconds = 0
        assertEquals(0f, t.progress, 0.0001f)
    }

    @Test
    fun `progress guards against division by zero`() {
        val t = timer(0)
        assertEquals(0f, t.progress, 0.0001f)
    }

    @Test
    fun `formattedTime pads seconds`() {
        val t = timer(125)
        assertEquals("2:05", t.formattedTime())
    }

    @Test
    fun `formattedTime for sub-minute values`() {
        val t = timer(9)
        assertEquals("0:09", t.formattedTime())
    }

    @Test
    fun `formattedTime for exact minutes`() {
        val t = timer(600)
        assertEquals("10:00", t.formattedTime())
    }

    @Test
    fun `reset restores initial running state`() {
        val t = timer(45)
        t.remainingSeconds = 5
        t.isRunning = false
        t.isFinished = true

        t.reset()

        assertEquals(45, t.remainingSeconds)
        assertTrue(t.isRunning)
        assertFalse(t.isFinished)
    }
}
