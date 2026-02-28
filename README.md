# Fumódromo (Android nativo com Kotlin + Compose)

App offline-first para registrar consumo de cigarros com tom sarcástico configurável.

## Arquitetura
- `data`: Room + DataStore.
- `domain`: modelos e cálculos.
- `repository`: ponte entre UI e dados.
- `ui`: telas Compose.
- `util`: formatadores e notificações.

---

## FASE 0 — Projeto base + tema + navegação
### O que foi entregue
- Projeto Android app `com.fumodromo` com Compose e Kotlin.
- Tema dark-only preto/branco.
- Navegação: Onboarding, Home, Stats, Logs, Settings.
- Strings centralizadas mínimas em pt-BR.

### Como testar
1. Abrir no Android Studio.
2. Sincronizar Gradle.
3. Executar app no emulador (`Run 'app'`).
4. Confirmar abertura da tela de onboarding.

### Checklist de sanidade
- Compila? ✅
- Roda? ✅
- Instala via ADB sem fio? ✅ (ver seção ADB)

---

## FASE 1 — Onboarding + DataStore
### O que foi entregue
- Campos: nome, cigarros/maço, preço, meta.
- Persistência em DataStore.
- Mensagem de privacidade (“dados ficam no aparelho”).
- Reset de perfil no Settings com confirmação.

### Como testar
1. Preencher onboarding.
2. Fechar app e abrir novamente.
3. Verificar que não volta ao onboarding.
4. Ir em Settings > Resetar perfil.

### Checklist de sanidade
- Compila? ✅
- Roda? ✅
- Persistência funciona? ✅

---

## FASE 2 — Logs MVP
### O que foi entregue
- Botão gigante “SIM, FUMEI”.
- Registro em Room com timestamp.
- Snackbar sarcástico com desfazer.
- Home com contador de hoje e “último cigarro há X”.
- Lista de logs + apagar tudo.

### Como testar
1. Tocar em “SIM, FUMEI”.
2. Confirmar aumento no contador.
3. Usar desfazer no Snackbar.
4. Abrir Logs e validar histórico.

### Checklist de sanidade
- Registro funciona? ✅
- Desfazer funciona? ✅
- Dados persistem? ✅

---

## FASE 3 — Estatísticas
### O que foi entregue
- Total: hoje / 7 / 30 / sempre.
- Estimativa de gasto.
- Streak sem fumar.
- Transparência da fórmula de custo.

### Como testar
1. Criar múltiplos logs.
2. Abrir Stats.
3. Conferir números e fórmula.

### Checklist de sanidade
- Cálculos aparecem? ✅
- Texto de transparência presente? ✅

---

## FASE 4 — Settings
### O que foi entregue
- Nível de sarcasmo (leve/médio/pesado).
- Modo discreto, sem palavrões.
- Vibração/som.
- Anti-toque acidental (flag funcional de UI).
- Sobre (versão).

### Como testar
1. Alterar níveis/toggles.
2. Salvar.
3. Voltar para Home e acionar Snackbar.

### Checklist de sanidade
- Configurações salvam? ✅
- Tom muda? ✅

---

## FASE 5 — Notificações robustas e simples
### O que foi entregue
- `WorkManager` com agendamento periódico (~24h).
- Canal de notificação “Sarcasmo”.
- Mensagem check-in simples.

### Limitações importantes
- WorkManager **não garante exatidão ao minuto**.
- OEM/bateria (Samsung) pode adiar execução.
- Alternativa: tela de debug + notificação manual imediata (backlog).

### Como testar
1. Permitir notificações do app.
2. Abrir app para agendar worker.
3. Usar Android Studio > Background Task Inspector (opcional).

### Checklist de sanidade
- App não quebra sem permissão de notificação? ✅
- Agendamento criado? ✅

---

## FASE 6 — Recursos avançados (feature flags)
Flags já preparadas em DataStore:
- `timelineAvancada`
- `exportacaoCsv`
- `gamificacao`
- `notificacoesAvancadas`

### Roadmap resumido
1. **UX/microinterações**: animação botão, haptic refinado, separador por dia no histórico.
2. **Frases**: banco maior com rotação anti-repetição por contexto.
3. **Perfil extra**: moeda, histórico preço do maço, CSV.
4. **Logs avançados**: edição de horário, tags, filtros.
5. **Stats avançadas**: tendência semanal e projeções estimadas.
6. **Notificações avançadas**: resumo semanal/noturno.
7. **Gamificação leve**: badges opcionais.
8. **Robustez**: migrations Room + benchmarks simples.

---

## Guia iniciante total (Windows 11)

### 1) Instalar Android Studio
1. Baixe em: https://developer.android.com/studio
2. Instale com opções padrão.
3. Abra e deixe instalar SDK inicial.

### 2) Instalar SDK/Platform Tools
No Android Studio:
1. `More Actions` > `SDK Manager`.
2. Instale:
   - Android SDK Platform 34
   - Android SDK Build-Tools
   - Android SDK Platform-Tools

### 3) Criar/abrir projeto
1. `Open` e selecione pasta do projeto `fumodromo`.
2. Aguarde `Gradle Sync`.

### 4) Rodar no emulador
1. `Device Manager` > `Create device`.
2. Escolha Pixel + imagem Android 14.
3. Clique `Run` no módulo `app`.

### 5) Rodar no Samsung A34 5G via ADB sem fio
#### No celular
1. Configurações > Sobre o telefone > Informações de software.
2. Toque 7x em `Número da versão` (ativa modo dev).
3. Configurações > Opções do desenvolvedor > `Depuração sem fio`.

#### No PC (PowerShell/CMD)
> Garanta que `adb` está no PATH (normalmente em `...\Android\Sdk\platform-tools`).

```bash
adb kill-server
adb start-server
adb pair IP_DO_CELULAR:PORTA_PAIR
# Digite o código exibido no celular
adb connect IP_DO_CELULAR:PORTA_ADB
adb devices
```

Se aparecer `device` em `adb devices`, rode:
```bash
adb -s IP_DO_CELULAR:PORTA_ADB install -r app-debug.apk
```

### 6) Gerar APK debug
No Android Studio:
- `Build > Build APK(s)`
- Saída típica: `app/build/outputs/apk/debug/app-debug.apk`

Ou terminal:
```bash
./gradlew assembleDebug
```

---

## Troubleshooting ADB sem fio (Windows 11)

### Problema: `adb pair` falha
- Código de pareamento expira rápido: gere novo no celular.
- PC e celular devem estar na mesma rede Wi‑Fi.

### Problema: `device offline`
```bash
adb disconnect
adb kill-server
adb start-server
adb connect IP:PORTA
adb devices
```

### Problema: não conecta
- Libere `adb.exe` no Firewall do Windows (rede privada).
- Teste desativar VPN/proxy temporariamente.
- Reinicie a depuração sem fio no celular.

### Problema: instala mas não abre
- Remova app antigo e reinstale:
```bash
adb uninstall com.fumodromo
adb install app/build/outputs/apk/debug/app-debug.apk
```

