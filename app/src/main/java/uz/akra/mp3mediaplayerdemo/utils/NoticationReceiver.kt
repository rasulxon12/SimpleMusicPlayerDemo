package uz.akra.mp3mediaplayerdemo.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NoticationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(1)

    }
}