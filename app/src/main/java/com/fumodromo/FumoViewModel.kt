package com.fumodromo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fumodromo.domain.Configuracoes
import com.fumodromo.domain.LogFumo
import com.fumodromo.domain.Perfil
import com.fumodromo.domain.StatsCalculator
import com.fumodromo.repository.FumoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class HomeUiState(
    val perfil: Perfil = Perfil(),
    val configuracoes: Configuracoes = Configuracoes(),
    val logs: List<LogFumo> = emptyList(),
    val ultimoLog: LogFumo? = null,
    val fraseAtual: String = "",
)

class FumoViewModel(private val repository: FumoRepository) : ViewModel() {
    private val calculator = StatsCalculator()
    private val frasesRecentes = ArrayDeque<String>()
    private val _ultimoLogCriado = MutableStateFlow<Long?>(null)
    val ultimoLogCriado: StateFlow<Long?> = _ultimoLogCriado

    val state: StateFlow<HomeUiState> = combine(
        repository.perfil,
        repository.configuracoes,
        repository.logs,
        repository.ultimoLog,
    ) { perfil, cfg, logs, ultimo ->
        HomeUiState(
            perfil = perfil,
            configuracoes = cfg,
            logs = logs,
            ultimoLog = ultimo,
            fraseAtual = escolherFrase(cfg),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun salvarPerfil(nome: String, cigarrosPorMaco: Int, precoMaco: Double, meta: Int?) = viewModelScope.launch {
        repository.salvarPerfil(nome, cigarrosPorMaco, precoMaco, meta)
    }

    fun registrarFumo() = viewModelScope.launch {
        _ultimoLogCriado.value = repository.registrarAgora()
    }

    fun desfazerUltimo() = viewModelScope.launch {
        _ultimoLogCriado.value?.let { repository.desfazer(it) }
    }

    fun apagarTudo() = viewModelScope.launch { repository.apagarTudo() }

    fun salvarConfiguracoes(cfg: Configuracoes) = viewModelScope.launch { repository.salvarConfiguracoes(cfg) }
    fun resetarPerfil() = viewModelScope.launch { repository.resetarPerfil() }

    fun totalHoje(logs: List<LogFumo>): Int {
        val hoje = LocalDate.now()
        return logs.count { LocalDate.ofInstant(it.instante, ZoneId.systemDefault()) == hoje }
    }

    fun custoHoje(logs: List<LogFumo>, perfil: Perfil): Double {
        return calculator.gastoEstimado(totalHoje(logs), perfil.precoMaco, perfil.cigarrosPorMaco)
    }

    fun streak(logs: List<LogFumo>): Int = calculator.streakSemFumar(logs.map { it.instante }, Instant.now())

    private fun escolherFrase(cfg: Configuracoes): String {
        val base = if (cfg.modoDiscreto) {
            listOf("Registro feito.", "Anotado sem drama.", "Dados atualizados.")
        } else {
            when (cfg.nivelSarcasmo) {
                com.fumodromo.domain.NivelSarcasmo.LEVE -> listOf("Mais um pro placar. Que fase.", "Respira... e bora registrar.")
                com.fumodromo.domain.NivelSarcasmo.MEDIO -> listOf("Parabéns pelo comprometimento com o hábito.", "Seu pulmão mandou um áudio de 8 minutos.")
                com.fumodromo.domain.NivelSarcasmo.PESADO -> listOf("Brilhante decisão. De novo.", "Esse botão já sabe seu nome.")
            }
        }
        val frase = base.firstOrNull { !frasesRecentes.contains(it) } ?: base.random()
        frasesRecentes.addLast(frase)
        if (frasesRecentes.size > 3) frasesRecentes.removeFirst()
        return frase
    }
}

class FumoViewModelFactory(private val repository: FumoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FumoViewModel(repository) as T
    }
}
