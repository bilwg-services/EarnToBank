package com.deucate.earntobank.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.deucate.earntobank.DatabseHalper
import com.deucate.earntobank.HomeActivity
import com.deucate.earntobank.Util
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.ValidationEnforcer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
import android.annotation.SuppressLint
import com.deucate.earntobank.R
import java.text.SimpleDateFormat
import java.util.*


class FirebaseService : FirebaseMessagingService() {

    private lateinit var database: SQLiteDatabase

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        val dbHelper = DatabseHalper(this, Util.tableName, null, 1)
        database = dbHelper.writableDatabase

        Timber.d("From: ${remoteMessage?.from}")

        val isEmpty = remoteMessage!!.data

        isEmpty.isNotEmpty().let {
            Timber.d("Message data payload: %s", remoteMessage.data)
            scheduleJob()
        }

        remoteMessage.notification?.let {
            Timber.d("Message Notification Body: ${it.body}")
            sendNotification(it)
            addToDatabase(it)
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun addToDatabase(data: RemoteMessage.Notification) {
        val row = ContentValues()
        row.put(Util.message, data.body)
        row.put(Util.title, data.title)
        row.put(
            Util.time,
            SimpleDateFormat("dd/MM/yy hh:mm aa").format(Calendar.getInstance().time)
        )

        val added = database.insert(Util.tableName, null, row)
        Timber.d("------>$added")
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

    private fun sendNotification(notification: RemoteMessage.Notification) {
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
            .setContentText(notification.body)
            .setContentTitle(notification.title)
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