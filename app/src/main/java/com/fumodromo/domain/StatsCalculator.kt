package com.fumodromo.domain

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class StatsCalculator(private val zoneId: ZoneId = ZoneId.systemDefault()) {

    fun custoPorCigarro(precoMaco: Double, cigarrosPorMaco: Int): Double {
        if (cigarrosPorMaco <= 0) return 0.0
        return precoMaco / cigarrosPorMaco
    }

    fun gastoEstimado(totalCigarros: Int, precoMaco: Double, cigarrosPorMaco: Int): Double {
        return totalCigarros * custoPorCigarro(precoMaco, cigarrosPorMaco)
    }

    fun streakSemFumar(logs: List<Instant>, agora: Instant = Instant.now()): Int {
        val diasComFumo = logs.map { LocalDate.ofInstant(it, zoneId) }.toSet()
        var cursor = LocalDate.ofInstant(agora, zoneId)
        var streak = 0
        while (!diasComFumo.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    fun tempoDesdeUltimo(ultimo: Instant?, agora: Instant = Instant.now()): Duration {
        return if (ultimo == null) Duration.ZERO else Duration.between(ultimo, agora).coerceAtLeast(Duration.ZERO)
    }
}
