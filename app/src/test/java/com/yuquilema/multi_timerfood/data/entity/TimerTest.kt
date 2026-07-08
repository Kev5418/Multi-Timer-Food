package com.yuquilema.multi_timerfood.data.entity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Unit tests for the Room entity [Timer], covering its constructors, default values,
 * accessors and the generated [Timer.equals]/[Timer.hashCode] contract.
 */
class TimerTest {

    @Test
    fun `no-arg constructor applies defaults`() {
        val timer = Timer()
        assertEquals(0, timer.id)
        assertEquals("IDLE", timer.estado)
        assertEquals("Default", timer.sonido)
        assertEquals("default", timer.sonidoNotificacion)
        assertEquals(0L, timer.tiempoInicioMillis)
    }

    @Test
    fun `creation constructor sets IDLE state`() {
        val timer = Timer("Rice", 600, "Pasta")
        assertEquals("Rice", timer.nombreAlimento)
        assertEquals(600, timer.duracionSegundos)
        assertEquals("Pasta", timer.categoria)
        assertEquals("IDLE", timer.estado)
    }

    @Test
    fun `full constructor assigns all fields`() {
        val timer = Timer(7, "Steak", 300, "Meat", "RUNNING", "medium rare", "Bell")
        assertEquals(7, timer.id)
        assertEquals("Steak", timer.nombreAlimento)
        assertEquals(300, timer.duracionSegundos)
        assertEquals("Meat", timer.categoria)
        assertEquals("RUNNING", timer.estado)
        assertEquals("medium rare", timer.notas)
        assertEquals("Bell", timer.sonido)
    }

    @Test
    fun `setters update values`() {
        val timer = Timer()
        timer.id = 42
        timer.nombreAlimento = "Egg"
        timer.duracionSegundos = 180
        timer.categoria = "Eggs"
        timer.estado = "PAUSED"
        timer.notas = "soft boiled"
        timer.sonido = "Chime"
        timer.sonidoNotificacion = "alarm"
        timer.tiempoInicioMillis = 123456L

        assertEquals(42, timer.id)
        assertEquals("Egg", timer.nombreAlimento)
        assertEquals(180, timer.duracionSegundos)
        assertEquals("Eggs", timer.categoria)
        assertEquals("PAUSED", timer.estado)
        assertEquals("soft boiled", timer.notas)
        assertEquals("Chime", timer.sonido)
        assertEquals("alarm", timer.sonidoNotificacion)
        assertEquals(123456L, timer.tiempoInicioMillis)
    }

    @Test
    fun `equal timers are equal and share hashCode`() {
        val a = Timer(1, "Steak", 300, "Meat", "IDLE", "note", "Bell")
        val b = Timer(1, "Steak", 300, "Meat", "IDLE", "note", "Bell")
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `timers differing by a field are not equal`() {
        val base = Timer(1, "Steak", 300, "Meat", "IDLE", "note", "Bell")
        val differentState = Timer(1, "Steak", 300, "Meat", "RUNNING", "note", "Bell")
        assertNotEquals(base, differentState)
    }

    @Test
    fun `equals is reflexive and type-safe`() {
        val timer = Timer(1, "Steak", 300, "Meat", "IDLE", "note", "Bell")
        assertEquals(timer, timer)
        assertNotEquals(timer, "not a timer")
    }
}
