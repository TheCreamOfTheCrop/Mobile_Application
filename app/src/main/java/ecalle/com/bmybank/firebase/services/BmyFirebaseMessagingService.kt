package ecalle.com.bmybank.firebase.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ecalle.com.bmybank.MainActivity
import ecalle.com.bmybank.R
import ecalle.com.bmybank.extensions.log


/**
 * Created by Thomas Ecalle on 24/05/2018.
 */
class BmyFirebaseMessagingService : FirebaseMessagingService()
{
    private val TAG = "MyFirebaseMsgService"
    private val BROADCAST_NOTIFICATION_ID = 1

    override fun onDeletedMessages()
    {
        super.onDeletedMessages()
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage?)
    {

        var notificationBody: String? = ""
        var notificationTitle: String? = ""
        var notificationData = ""
        try
        {
            notificationData = remoteMessage!!.data.toString()
            notificationTitle = remoteMessage.notification!!.title
            notificationBody = remoteMessage.notification!!.body
        }
        catch (e: NullPointerException)
        {
            log("onMessageReceived: NullPointerException: " + e.message)
        }

        log("onMessageReceived: data: " + notificationData)
        log("onMessageReceived: notification body: " + notificationBody!!)
        log("onMessageReceived: notification title: " + notificationTitle!!)


        val dataType = remoteMessage!!.data[getString(R.string.data_type)]
        if (dataType == getString(R.string.direct_message))
        {
            log("onMessageReceived: new incoming message.")
            val title = remoteMessage.data[getString(R.string.data_title)]
            val message = remoteMessage.data[getString(R.string.data_message)]
            val messageId = remoteMessage.data[getString(R.string.data_message_id)]
            sendMessageNotification(
                    title ?: "",
                    message ?: "",
                    messageId ?: "")
        }
    }

    /**
     * Build a push notification for a chat message
     * @param title
     * @param message
     */
    private fun sendMessageNotification(title: String, message: String, messageId: String)
    {
        log("sendChatmessageNotification: building a chatmessage notification")

        //get the notification id
        val notificationId = buildNotificationId(messageId)

        // Instantiate a Builder object.
        val builder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))

        // Creates an Intent for the Activity
        val pendingIntent = Intent(this, MainActivity::class.java)
        // Sets the Activity to start in a new, empty task
        pendingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // Creates the PendingIntent
        val notifyPendingIntent = PendingIntent.getActivity(
                this,
                0,
                pendingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        //add properties to the builder
        builder.setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources,
                        R.drawable.logo))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setColor(getColor(R.color.colorPrimary))
                .setAutoCancel(true)
                //.setSubText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setOnlyAlertOnce(true)

        builder.setContentIntent(notifyPendingIntent)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(notificationId, builder.build())

    }


    private fun buildNotificationId(id: String): Int
    {
        log("buildNotificationId: building a notification id.")

        var notificationId = 0
        for (i in 0..8)
        {
            notificationId = notificationId + id[0].toInt()
        }
        log("buildNotificationId: id: " + id)
        log("buildNotificationId: notification id:" + notificationId)
        return notificationId
    }
}