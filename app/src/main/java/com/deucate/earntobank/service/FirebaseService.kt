package com.deucate.earntobank.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.deucate.earntobank.HomeActivity
import com.deucate.earntobank.R
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.ValidationEnforcer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        Timber.d("From: ${remoteMessage?.from}")

        remoteMessage?.data?.isNotEmpty()?.let {
            Timber.d("Message data payload: %s", remoteMessage.data)
            scheduleJob()
        }

        remoteMessage?.notification?.let {
            Timber.d("Message Notification Body: ${it.body}")
            sendNotification(it.body!!)
        }

    }

    private fun scheduleJob() {
        try {
            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
            val myJob = dispatcher.newJobBuilder()
                .setTag("my-job-tag")
                .build()
            dispatcher.schedule(myJob)
        } catch (e: ValidationEnforcer.ValidationException) {
            e.printStackTrace()
        }
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

}