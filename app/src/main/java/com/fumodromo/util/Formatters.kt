package com.fumodromo.util

import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val moedaBr = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
private val dateTimeFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

fun formatarMoeda(valor: Double): String = moedaBr.format(valor)

fun formatarDataHora(instante: Instant): String = dateTimeFmt.format(instante.atZone(ZoneId.systemDefault()))

fun formatarDuracao(d: Duration): String {
    val min = d.toMinutes()
    if (min < 60) return "há ${min} min"
    val horas = d.toHours()
    if (horas < 24) return "há ${horas} h"
    return "há ${d.toDays()} dias"
}
