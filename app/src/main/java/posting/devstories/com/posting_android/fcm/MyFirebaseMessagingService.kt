package posting.devstories.com.posting_android.fcm


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.activities.IntroActivity

/**
 * Created by dev1 on 2017-12-15.
 */

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val mHandler: Handler

    init {

        mHandler = Handler()
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        if (remoteMessage == null) {
            return
        }

        val data = remoteMessage.data ?: return

        val channelId = getString(R.string.app_name)

        val title = data["title"]
        val body = data["body"]
        val group = channelId

        val intent = Intent(this, IntroActivity::class.java)
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("posting_id", data["posting_id"])
        intent.putExtra("chatting_member_id", data["chatting_member_id"])
        intent.putExtra("FROM_PUSH", true)


        val pendingIntent =
            PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())
            .setVibrate(longArrayOf(1000, 1000))
            .setGroup(group)
            .setContentIntent(pendingIntent)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationBuilder.setSound(defaultSoundUri)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notificationManager, channelId, title, body)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val gnotificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setGroup(group)
                .setGroupSummary(true)
                .setAutoCancel(true)

            notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
            notificationManager.notify(group, 0, gnotificationBuilder.build())
        } else {
            notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannel(
        notificationManager: NotificationManager,
        channelId: String,
        title: String?,
        body: String?
    ) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val mChannel = NotificationChannel(channelId, title, importance)
        mChannel.description = body
        mChannel.enableLights(true)
        mChannel.lightColor = Color.BLUE
        notificationManager.createNotificationChannel(mChannel)

    }

    companion object {
        private val TAG = "MyFirebaseMsgService"
    }

}
