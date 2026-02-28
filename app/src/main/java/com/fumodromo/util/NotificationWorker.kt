package com.fumodromo.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Lembretes Fumódromo", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Lembretes com humor ácido para check-in diário"
            },
        )

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return Result.success()
        }

        val frase = FRASES.random(Random(System.currentTimeMillis()))
        val notificacao = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Check-in do Fumódromo")
            .setContentText(frase)
            .setStyle(NotificationCompat.BigTextStyle().bigText(frase))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(1101, notificacao)
        return Result.success()
    }

    companion object {
        private const val CHANNEL_ID = "sarcasmo"
        private val FRASES = listOf(
            "Hora do check-in. Fingir que não fuma ainda não reduziu o número.",
            "Seu pulmão pediu transparência: registra como foi o dia.",
            "Clima fumódromo: triste, cômico e estatisticamente útil.",
            "Sem julgamento. Só números, ironia e um pequeno choque de realidade.",
        )

        fun agendar(context: Context) {
            val constraints = Constraints.Builder().setRequiresBatteryNotLow(false).build()
            val request = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "checkin_diario",
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }
    }
}
