package com.harrowhaus.crypseal.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class CrypsealNotificationManager(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val CHANNEL_ID = "crypseal_agent_status"

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Agent Status",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    fun showApprovalNeeded(sessionId: String, toolName: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Crypseal: Approval Required")
            .setContentText("Agent requests permission to run: $toolName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
            
        notificationManager.notify(sessionId.hashCode(), notification)
    }

    fun showActiveRun(sessionId: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Crypseal: Agent Active")
            .setContentText("Executing autonomous run...")
            .setOngoing(true)
            .build()
            
        notificationManager.notify(sessionId.hashCode(), notification)
    }

    fun clearActiveRun(sessionId: String) {
        notificationManager.cancel(sessionId.hashCode())
    }
}
