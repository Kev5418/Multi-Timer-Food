package com.yuquilema.multi_timerfood.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the Room entity [TimerHistoryItem], verifying its default state
 * and accessor round-trips.
 */
class TimerHistoryItemTest {

    @Test
    fun `defaults are empty`() {
        val item = TimerHistoryItem()
        assertEquals(0, item.id)
        assertEquals(0, item.totalSeconds)
        assertEquals(0L, item.dateMillis)
        assertFalse(item.isCompleted)
    }

    @Test
    fun `setters round-trip`() {
        val item = TimerHistoryItem()
        item.id = 5
        item.foodName = "Pasta"
        item.category = "Pasta"
        item.totalSeconds = 480
        item.setCompleted(true)
        item.dateMillis = 999L

        assertEquals(5, item.id)
        assertEquals("Pasta", item.foodName)
        assertEquals("Pasta", item.category)
        assertEquals(480, item.totalSeconds)
        assertTrue(item.isCompleted)
        assertEquals(999L, item.dateMillis)
    }
}
