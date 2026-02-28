package com.fumodromo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import com.fumodromo.domain.NivelSarcasmo
import com.fumodromo.ui.navigation.NavRoutes
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Bem-vindo ao Fumódromo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Vamos montar um plano realista pra você entender seus padrões e reduzir com contexto.")
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome (opcional)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = cigarrosPorMaco, onValueChange = { cigarrosPorMaco = it.filter(Char::isDigit) }, label = { Text("Cigarros por maço") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = precoMaco, onValueChange = { precoMaco = it.replace(',', '.') }, label = { Text("Preço do maço (R$)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = meta, onValueChange = { meta = it.filter(Char::isDigit) }, label = { Text("Meta por dia (opcional)") }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
        item {
            Button(
                onClick = {
                    vm.salvarPerfil(
                        nome = nome,
                        cigarrosPorMaco = cigarrosPorMaco.toIntOrNull() ?: 20,
                        precoMaco = precoMaco.toDoubleOrNull() ?: 0.0,
                        meta = meta.toIntOrNull(),
                    )
                    onConcluir()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) { Text("Começar") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: FumoViewModel, irPara: (String) -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    val snack = remember { SnackbarHostState() }
    val totalHoje = vm.totalHoje(state.logs)
    val metaHoje = state.perfil.metaCigarrosDia
    val progressoMeta = when {
        metaHoje == null || metaHoje <= 0 -> 0f
        else -> (totalHoje.toFloat() / metaHoje.toFloat()).coerceIn(0f, 1f)
    }

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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Hoje", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("${totalHoje} cigarro(s) registrado(s)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (state.ultimoLog == null) "Ainda sem registro hoje"
                            else "Último há ${formatarDuracao(Duration.between(state.ultimoLog!!.instante, Instant.now()))}",
                        )
                        if (metaHoje != null && metaHoje > 0) {
                            Text("Meta diária: $metaHoje")
                            LinearProgressIndicator(progress = { progressoMeta }, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
            item {
                Button(onClick = { vm.registrarFumo() }, modifier = Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(18.dp)) {
                    Text("Registrar cigarro")
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    NavButton("Estatísticas") { irPara(NavRoutes.Stats) }
                    NavButton("Histórico") { irPara(NavRoutes.Logs) }
                    NavButton("Config") { irPara(NavRoutes.Settings) }
                }
            }
            item {
                InsightCard(
                    titulo = "Custo estimado hoje",
                    valor = formatarMoeda(vm.custoHoje(state.logs, state.perfil)),
                    detalhe = "Baseado no preço por maço que você definiu",
                )
            }
        }
    }
}

@Composable
fun LogsScreen(vm: FumoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var confirmar by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Histórico", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            TextButton(onClick = { confirmar = true }) { Text("Apagar tudo") }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.logs) {
                ElevatedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(formatarDataHora(it.instante), style = MaterialTheme.typography.titleMedium)
                            Text("Registro manual", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        TextButton(onClick = { vm.desfazerUltimo() }, modifier = Modifier.align(Alignment.End)) {
            Text("Desfazer último registro")
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

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Estatísticas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill("Hoje", vm.totalHoje(logs).toString())
                StatPill("7 dias", logs.take(7).size.toString())
                StatPill("30 dias", logs.take(30).size.toString())
            }
        }
        item { StatPill("Total", logs.size.toString(), modifier = Modifier.fillMaxWidth()) }
        item { HorizontalDivider() }
        item {
            InsightCard(
                titulo = "Gasto hoje",
                valor = formatarMoeda(vm.custoHoje(logs, state.perfil)),
                detalhe = "custo por cigarro = preço do maço / cigarros por maço",
            )
        }
        item {
            InsightCard(
                titulo = "Streak sem fumar",
                valor = "${vm.streak(logs)} dia(s)",
                detalhe = "quanto maior, melhor",
            )
        }
    }
}

@Composable
fun SettingsScreen(vm: FumoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var cfg by remember(state.configuracoes) { mutableStateOf(state.configuracoes) }
    var confirmarReset by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Configurações", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item {
            ElevatedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nível de sarcasmo")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NivelSarcasmo.entries.forEach { n ->
                            FilterChip(
                                selected = cfg.nivelSarcasmo == n,
                                onClick = { cfg = cfg.copy(nivelSarcasmo = n) },
                                label = { Text(n.name) },
                            )
                        }
                    }
                }
            }
        }
        item { Toggle("Modo discreto", cfg.modoDiscreto) { cfg = cfg.copy(modoDiscreto = it) } }
        item { Toggle("Sem palavrões", cfg.semPalavroes) { cfg = cfg.copy(semPalavroes = it) } }
        item { Toggle("Vibração", cfg.vibracaoAtiva) { cfg = cfg.copy(vibracaoAtiva = it) } }
        item { Toggle("Som", cfg.somAtivo) { cfg = cfg.copy(somAtivo = it) } }
        item { Toggle("Anti toque acidental", cfg.antiToqueAcidental) { cfg = cfg.copy(antiToqueAcidental = it) } }
        item {
            Button(onClick = { vm.salvarConfiguracoes(cfg) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Salvar configurações")
            }
        }
        item { TextButton(onClick = { confirmarReset = true }) { Text("Resetar perfil") } }
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
private fun StatPill(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(titulo, style = MaterialTheme.typography.labelLarge)
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InsightCard(titulo: String, valor: String, detalhe: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelLarge)
            Text(valor, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(detalhe, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun NavButton(texto: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(14.dp)) {
        Text(texto)
    }
}

@Composable
private fun Toggle(titulo: String, marcado: Boolean, onChange: (Boolean) -> Unit) {
    Surface(shape = RoundedCornerShape(14.dp), tonalElevation = 2.dp) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(titulo)
            Switch(checked = marcado, onCheckedChange = onChange)
        }
    }
}
