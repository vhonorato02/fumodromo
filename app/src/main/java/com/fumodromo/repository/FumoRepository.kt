package com.fumodromo.repository

import com.fumodromo.data.datastore.SettingsDataStore
import com.fumodromo.data.local.SmokeLogDao
import com.fumodromo.data.local.SmokeLogEntity
import com.fumodromo.domain.LogFumo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class FumoRepository(
    private val dao: SmokeLogDao,
    private val settings: SettingsDataStore,
) {
    val perfil = settings.perfil
    val configuracoes = settings.configuracoes
    val flags = settings.flags

    val logs: Flow<List<LogFumo>> = dao.observeAll().map { items ->
        items.map { LogFumo(it.id, Instant.ofEpochMilli(it.timestampMillis)) }
    }

    val ultimoLog = dao.observeLast().map { item ->
        item?.let { LogFumo(it.id, Instant.ofEpochMilli(it.timestampMillis)) }
    }

    suspend fun registrarAgora(): Long {
        return dao.insert(SmokeLogEntity(timestampMillis = Instant.now().toEpochMilli()))
    }

    suspend fun desfazer(id: Long) = dao.deleteById(id)
    suspend fun apagarTudo() = dao.deleteAll()

    suspend fun salvarPerfil(nome: String, cigarrosPorMaco: Int, precoMaco: Double, meta: Int?) {
        settings.salvarPerfil(
            com.fumodromo.domain.Perfil(
                nome = nome,
                cigarrosPorMaco = cigarrosPorMaco,
                precoMaco = precoMaco,
                metaPorDia = meta,
                onboardingConcluido = true,
            )
        )
    }

    suspend fun salvarConfiguracoes(cfg: com.fumodromo.domain.Configuracoes) = settings.salvarConfiguracoes(cfg)
    suspend fun resetarPerfil() = settings.resetarPerfil()
}
