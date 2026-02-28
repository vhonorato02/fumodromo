package com.fumodromo.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fumodromo.domain.Configuracoes
import com.fumodromo.domain.FlagRecursos
import com.fumodromo.domain.NivelSarcasmo
import com.fumodromo.domain.Perfil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "fumodromo_settings")

class SettingsDataStore(private val context: Context) {
    private object Keys {
        val nome = stringPreferencesKey("nome")
        val cigarrosPorMaco = intPreferencesKey("cigarros_por_maco")
        val precoMaco = stringPreferencesKey("preco_maco")
        val metaPorDia = intPreferencesKey("meta_por_dia")
        val onboardingConcluido = booleanPreferencesKey("onboarding_concluido")

        val nivelSarcasmo = stringPreferencesKey("nivel_sarcasmo")
        val modoDiscreto = booleanPreferencesKey("modo_discreto")
        val semPalavroes = booleanPreferencesKey("sem_palavroes")
        val vibracaoAtiva = booleanPreferencesKey("vibracao_ativa")
        val somAtivo = booleanPreferencesKey("som_ativo")
        val antiToqueAcidental = booleanPreferencesKey("anti_toque")
        val inicioDia = intPreferencesKey("inicio_dia")

        val flagTimelineAvancada = booleanPreferencesKey("flag_timeline")
        val flagExportacaoCsv = booleanPreferencesKey("flag_exportacao")
        val flagGamificacao = booleanPreferencesKey("flag_gamificacao")
        val flagNotificacoesAvancadas = booleanPreferencesKey("flag_notif_avancadas")
    }

    val perfil: Flow<Perfil> = context.dataStore.data.map { pref ->
        Perfil(
            nome = pref[Keys.nome].orEmpty(),
            cigarrosPorMaco = pref[Keys.cigarrosPorMaco] ?: 20,
            precoMaco = pref[Keys.precoMaco]?.toDoubleOrNull() ?: 0.0,
            metaPorDia = pref[Keys.metaPorDia],
            onboardingConcluido = pref[Keys.onboardingConcluido] ?: false,
        )
    }

    val configuracoes: Flow<Configuracoes> = context.dataStore.data.map { pref ->
        Configuracoes(
            nivelSarcasmo = runCatching { NivelSarcasmo.valueOf(pref[Keys.nivelSarcasmo] ?: NivelSarcasmo.LEVE.name) }
                .getOrDefault(NivelSarcasmo.LEVE),
            modoDiscreto = pref[Keys.modoDiscreto] ?: false,
            semPalavroes = pref[Keys.semPalavroes] ?: true,
            vibracaoAtiva = pref[Keys.vibracaoAtiva] ?: true,
            somAtivo = pref[Keys.somAtivo] ?: false,
            antiToqueAcidental = pref[Keys.antiToqueAcidental] ?: false,
            inicioDiaHora = pref[Keys.inicioDia] ?: 4,
        )
    }

    val flags: Flow<FlagRecursos> = context.dataStore.data.map { pref ->
        FlagRecursos(
            timelineAvancada = pref[Keys.flagTimelineAvancada] ?: false,
            exportacaoCsv = pref[Keys.flagExportacaoCsv] ?: false,
            gamificacao = pref[Keys.flagGamificacao] ?: false,
            notificacoesAvancadas = pref[Keys.flagNotificacoesAvancadas] ?: false,
        )
    }

    suspend fun salvarPerfil(perfil: Perfil) {
        context.dataStore.edit {
            it[Keys.nome] = perfil.nome
            it[Keys.cigarrosPorMaco] = perfil.cigarrosPorMaco
            it[Keys.precoMaco] = perfil.precoMaco.toString()
            perfil.metaPorDia?.let { meta -> it[Keys.metaPorDia] = meta }
            it[Keys.onboardingConcluido] = perfil.onboardingConcluido
        }
    }

    suspend fun resetarPerfil() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun salvarConfiguracoes(cfg: Configuracoes) {
        context.dataStore.edit {
            it[Keys.nivelSarcasmo] = cfg.nivelSarcasmo.name
            it[Keys.modoDiscreto] = cfg.modoDiscreto
            it[Keys.semPalavroes] = cfg.semPalavroes
            it[Keys.vibracaoAtiva] = cfg.vibracaoAtiva
            it[Keys.somAtivo] = cfg.somAtivo
            it[Keys.antiToqueAcidental] = cfg.antiToqueAcidental
            it[Keys.inicioDia] = cfg.inicioDiaHora
        }
    }
}
