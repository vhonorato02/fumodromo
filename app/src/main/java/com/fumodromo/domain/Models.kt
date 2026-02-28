package com.fumodromo.domain

import java.time.Instant

enum class NivelSarcasmo { LEVE, MEDIO, PESADO }

data class Perfil(
    val nome: String = "",
    val cigarrosPorMaco: Int = 20,
    val precoMaco: Double = 0.0,
    val metaPorDia: Int? = null,
    val onboardingConcluido: Boolean = false,
)

data class Configuracoes(
    val nivelSarcasmo: NivelSarcasmo = NivelSarcasmo.LEVE,
    val modoDiscreto: Boolean = false,
    val semPalavroes: Boolean = true,
    val vibracaoAtiva: Boolean = true,
    val somAtivo: Boolean = false,
    val antiToqueAcidental: Boolean = false,
    val inicioDiaHora: Int = 4,
)

data class LogFumo(
    val id: Long,
    val instante: Instant,
)

data class FlagRecursos(
    val timelineAvancada: Boolean = false,
    val exportacaoCsv: Boolean = false,
    val gamificacao: Boolean = false,
    val notificacoesAvancadas: Boolean = false,
)
