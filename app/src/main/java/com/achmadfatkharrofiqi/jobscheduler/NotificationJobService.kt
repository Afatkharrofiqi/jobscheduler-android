package com.achmadfatkharrofiqi.jobscheduler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat


class NotificationJobService : JobService() {

    companion object{
        val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        val NOTIFICATION_ID = 0
    }

    private lateinit var mNotifyManager: NotificationManager

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        // Create the notification channel
        createNotificationChannel()

        // Set up the notification content intent to launch the app when clicked
        val contentPendingIntent = PendingIntent.getActivity(
            this, NOTIFICATION_ID, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
        builder.apply {
            setContentTitle("Job Service")
            setContentText("Your Job ran in to completion!")
            setContentIntent(contentPendingIntent)
            setSmallIcon(R.drawable.ic_job_running)
            priority = NotificationCompat.PRIORITY_HIGH
            setDefaults(NotificationCompat.DEFAULT_ALL)
            setAutoCancel(true)
        }

        mNotifyManager.notify(NOTIFICATION_ID, builder.build())
        
        return false
    }

    private fun createNotificationChannel() {
        // Define notification manager object.
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Job Service notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "Notifications from Job Service"
            }
            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }
}