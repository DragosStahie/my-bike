package com.mybike

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random

class NotificationBroadcastReceiver : BroadcastReceiver() {
    private lateinit var context: Context
    private val notificationIntent by lazy {
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        val bikeName = intent.getStringExtra("bikeName").toString()
        val currentBikeDistanceToService = intent.getIntExtra("service", 0)
        val currentDistanceUnits = intent.getStringExtra("units").toString()

        sendNotification(bikeName, currentBikeDistanceToService, currentDistanceUnits)
    }

    private fun sendNotification(
        bikeName: String,
        currentBikeDistanceToService: Int,
        currentDistanceUnits: String
    ) {
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(
                    Random.nextInt(), createNotification(
                        bikeName, context.getString(
                            com.mybike.common.R.string.service_in_template,
                            currentBikeDistanceToService,
                            currentDistanceUnits
                        )
                    ).build()
                )
            }
        }
    }

    private fun createNotification(title: String, text: String) =
        NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setSmallIcon(com.mybike.common.R.drawable.ic_app)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setAutoCancel(true)
}