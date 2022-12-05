package com.example.projectmanage.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.projectmanage.R
import com.example.projectmanage.activities.MainActivity
import com.example.projectmanage.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//         [START_EXCLUDE]
//         There are two types of messages data messages and notification messages. Data messages are handled
//         here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
//         traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
//         is in the foreground. When the app is in the background an automatically generated notification is displayed.
//         When the user taps on the notification they are returned to the app. Messages containing both notification
//         and data payloads are treated as notification messages. The Firebase console always sends notification
//         messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
//         [END_EXCLUDE]
//
//         Handle FCM messages here.
//         Not getting messages here? See why this may be: https://goo.gl/39bRNJ


        // TODO (Step 7: Once the notification is sent successfully it will be received here.)
        // START
        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            // The notification data payload is printed in the log.

            // The Title and Message are assigned to the local variables
            val title = remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message = remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!

            // Finally sent them to build a notification.
            sendNotification(title, message)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("Token: ", token)
        saveTokenToServer(token)
    }

    private fun saveTokenToServer(token: String) {

    }

    private fun sendNotification(title:String,message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = Constants.NOTIFICATION_CHANNEL_ID
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification).setContentTitle("Title")
            .setContentText("Message").setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,"Channel ProjectManage Title",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0,notificationBuilder.build())

    }


}