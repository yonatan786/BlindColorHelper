package com.example.colorblindhelper.Classes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.colorblindhelper.Activities.MainActivity
import com.example.colorblindhelper.Activities.ViewImage
import com.example.colorblindhelper.Activities.viewOtherProfileActivity
import com.example.colorblindhelper.R
import com.example.colorblindhelper.getUserName
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage



class FirebaseMessageReceiver : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.

//            showNotification(remoteMessage.getData().get("title"),
//                          remoteMessage.getData().get("message"));
//        if(remoteMessage.data.isNotEmpty()){
//            showNotification(remoteMessage.data["title"], remoteMessage.data["body"])
//            val uname = remoteMessage.data["username"]
//            val fname = remoteMessage.data["fileName"]
//            val unameProfile = remoteMessage.data["userNameProfile"]
//            val title = remoteMessage.data["title"]
//
//            if (title == "Friend Request") {
//                val intent = Intent(applicationContext, viewOtherProfileActivity::class.java)
//                intent.putExtra("userNameProfile", unameProfile)
//                applicationContext.startActivity(intent)
//            } else if (title == "New Comment") {
//                val intent = Intent(applicationContext, ViewImage::class.java)
//                intent.putExtra("username", uname)
//                intent.putExtra("fileName", fname)
//                applicationContext.startActivity(intent)
//            }
//        }

        // Second case when notification payload is
        // received.
//        if (remoteMessage.notification != null) {
//            // Since the notification is received directly from
//            // FCM, the title and the body can be fetched
//            // directly as below.
//            showNotification(
//                remoteMessage.notification!!.title,
//                remoteMessage.notification!!.body
//            )
//        }
        if (remoteMessage.data.isNotEmpty()) {
            showNotification(
                remoteMessage.data["title"],
                remoteMessage.data["body"],
                remoteMessage.data["username"],
                remoteMessage.data["fileName"],
                remoteMessage.data["userNameProfile"]
            )
        }
    }

    // Method to get the custom Design for the display of
    // notification.
    private fun getCustomDesign(
        title: String?,
        message: String?
    ): RemoteViews {
        val remoteViews = RemoteViews(
            applicationContext.packageName,
            R.layout.notification
        )
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(
            R.id.icon,
            R.drawable.circle_cropped_icon
        )
        return remoteViews
    }

    // Method to display the notifications
    fun showNotification(
        title: String?,
        message: String?,
        username: String?,
        fileName: String?,
        myUserName: String?
    ) {
        // Pass the intent to switch to the MainActivity

        val intent = if (title == "Friend Request") {
            Intent(this, viewOtherProfileActivity::class.java)
        } else {
            Intent(this, ViewImage::class.java)
        }
        intent.putExtra("username", username)
        intent.putExtra("fileName", fileName)
        intent.putExtra("notification", true)
        intent.putExtra("userNameProfile", myUserName)
        // Assign channel ID
        val channel_id = "notification_channel"
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Pass the intent to PendingIntent to start the
        // next Activity
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            channel_id
        )
            .setSmallIcon(R.drawable.circle_cropped_icon)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setContent(
                getCustomDesign(title, message)
            )
        } // If Android Version is lower than Jelly Beans,
        else {
            builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.circle_cropped_icon)
        }
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                channel_id, "web_app",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }
        notificationManager.notify(0, builder.build())
    }
}