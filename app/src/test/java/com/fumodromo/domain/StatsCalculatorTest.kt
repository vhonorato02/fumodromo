package com.fumodromo.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class StatsCalculatorTest {
    private val calc = StatsCalculator(ZoneId.of("UTC"))

    @Test
    fun custoPorCigarro_calculaCorretamente() {
        assertThat(calc.custoPorCigarro(15.0, 20)).isWithin(0.0001).of(0.75)
    }

    @Test
    fun gastoEstimado_calculaCorretamente() {
        assertThat(calc.gastoEstimado(10, 20.0, 20)).isWithin(0.0001).of(10.0)
    }

    @Test
    fun streakSemFumar_retornaDiasSemRegistro() {
        val logs = listOf(
            Instant.parse("2025-01-10T10:00:00Z"),
            Instant.parse("2025-01-12T10:00:00Z"),
        )
        val streak = calc.streakSemFumar(logs, Instant.parse("2025-01-14T10:00:00Z"))
        assertThat(streak).isEqualTo(2)
    }
}
