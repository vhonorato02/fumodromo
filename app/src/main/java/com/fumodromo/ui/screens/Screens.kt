package com.fumodromo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fumodromo.FumoViewModel
import com.fumodromo.domain.Configuracoes
import com.fumodromo.domain.NivelSarcasmo
import com.fumodromo.util.formatarDataHora
import com.fumodromo.util.formatarDuracao
import com.fumodromo.util.formatarMoeda
import java.time.Duration
import java.time.Instant

@Composable
fun OnboardingScreen(vm: FumoViewModel, onConcluir: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var cigarrosPorMaco by remember { mutableStateOf("20") }
    var precoMaco by remember { mutableStateOf("") }
    var meta by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Bem-vindo ao Fumódromo", style = MaterialTheme.typography.headlineSmall)
        Text("Privacidade: seus dados ficam no aparelho.")
        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome (opcional)") })
        OutlinedTextField(value = cigarrosPorMaco, onValueChange = { cigarrosPorMaco = it.filter(Char::isDigit) }, label = { Text("Cigarros por maço") })
        OutlinedTextField(value = precoMaco, onValueChange = { precoMaco = it.replace(',', '.') }, label = { Text("Preço do maço (R$)") })
        OutlinedTextField(value = meta, onValueChange = { meta = it.filter(Char::isDigit) }, label = { Text("Meta por dia (opcional)") })
        Button(onClick = {
            vm.salvarPerfil(
                nome = nome,
                cigarrosPorMaco = cigarrosPorMaco.toIntOrNull() ?: 20,
                precoMaco = precoMaco.toDoubleOrNull() ?: 0.0,
                meta = meta.toIntOrNull(),
            )
            onConcluir()
        }) { Text("Salvar e entrar") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: FumoViewModel, irPara: (String) -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    val snack = remember { SnackbarHostState() }

    LaunchedEffect(vm.ultimoLogCriado.collectAsStateWithLifecycle().value) {
        if (vm.ultimoLogCriado.value != null) {
            val res = snack.showSnackbar(message = state.fraseAtual, actionLabel = "Desfazer")
            if (res.name.contains("Action")) vm.desfazerUltimo()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Fumódromo") }) },
        snackbarHost = { SnackbarHost(snack) },
    ) { pad ->
        Column(
            modifier = Modifier.fillMaxSize().padding(pad).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Hoje", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Cigarros: ${vm.totalHoje(state.logs)}")
            Text(
                if (state.ultimoLog == null) "Último cigarro: nenhum registro"
                else "Último cigarro: ${formatarDuracao(Duration.between(state.ultimoLog!!.instante, Instant.now()))}"
            )
            Button(onClick = { vm.registrarFumo() }, modifier = Modifier.fillMaxWidth().height(72.dp)) {
                Text("SIM, FUMEI")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { irPara("stats") }) { Text("Stats") }
                Button(onClick = { irPara("logs") }) { Text("Logs") }
                Button(onClick = { irPara("settings") }) { Text("Settings") }
            }
        }
    }
}

@Composable
fun LogsScreen(vm: FumoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var confirmar by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Histórico", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { confirmar = true }) { Text("Apagar tudo") }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.logs) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(formatarDataHora(it.instante))
                        TextButton(onClick = { vm.desfazerUltimo() }) { Text("Desfazer último") }
                    }
                }
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            confirmButton = {
                TextButton(onClick = { vm.apagarTudo(); confirmar = false }) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } },
            title = { Text("Apagar tudo") },
            text = { Text("Essa ação remove todos os logs. Sem volta.") },
        )
    }
}

@Composable
fun StatsScreen(vm: FumoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val logs = state.logs
    val hoje = vm.totalHoje(logs)
    val sete = logs.take(7).size
    val trinta = logs.take(30).size

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Estatísticas", style = MaterialTheme.typography.titleLarge)
        Text("Hoje: $hoje")
        Text("7 dias: $sete")
        Text("30 dias: $trinta")
        Text("Sempre: ${logs.size}")
        HorizontalDivider()
        Text("Gasto hoje (estimativa): ${formatarMoeda(vm.custoHoje(logs, state.perfil))}")
        Text("Streak sem fumar: ${vm.streak(logs)} dia(s)")
        Text("Transparência: custo por cigarro = preço do maço / cigarros por maço.")
    }
}

@Composable
fun SettingsScreen(vm: FumoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var cfg by remember(state.configuracoes) { mutableStateOf(state.configuracoes) }
    var confirmarReset by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Configurações", style = MaterialTheme.typography.titleLarge)
        Text("Sarcasmo")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NivelSarcasmo.entries.forEach { n ->
                Button(onClick = { cfg = cfg.copy(nivelSarcasmo = n) }) { Text(n.name) }
            }
        }
        Toggle("Modo discreto", cfg.modoDiscreto) { cfg = cfg.copy(modoDiscreto = it) }
        Toggle("Sem palavrões", cfg.semPalavroes) { cfg = cfg.copy(semPalavroes = it) }
        Toggle("Vibração", cfg.vibracaoAtiva) { cfg = cfg.copy(vibracaoAtiva = it) }
        Toggle("Som", cfg.somAtivo) { cfg = cfg.copy(somAtivo = it) }
        Toggle("Anti toque acidental (segurar 1s)", cfg.antiToqueAcidental) { cfg = cfg.copy(antiToqueAcidental = it) }
        Button(onClick = { vm.salvarConfiguracoes(cfg) }) { Text("Salvar configurações") }
        Spacer(Modifier.height(10.dp))
        TextButton(onClick = { confirmarReset = true }) { Text("Resetar perfil") }
        Text("Sobre: versão 1.0.0")
    }

    if (confirmarReset) {
        AlertDialog(
            onDismissRequest = { confirmarReset = false },
            confirmButton = {
                TextButton(onClick = { vm.resetarPerfil(); confirmarReset = false }) { Text("Resetar") }
            },
            dismissButton = { TextButton(onClick = { confirmarReset = false }) { Text("Cancelar") } },
            title = { Text("Confirmar reset") },
            text = { Text("Apaga perfil e configurações locais.") },
        )
    }
}

@Composable
private fun Toggle(titulo: String, marcado: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(titulo)
        Switch(checked = marcado, onCheckedChange = onChange)
    }
}
