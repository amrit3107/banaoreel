package com.banaoreel.app.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.banaoreel.app.data.api.BanaoReelApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Receives the push the backend sends (via PushNotificationService) when a video job completes/fails. */
@AndroidEntryPoint
class BanaoReelMessagingService : FirebaseMessagingService() {

    @Inject lateinit var api: BanaoReelApi

    private val channelId = "video_jobs"
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: "BanaoReel"
        val body = message.notification?.body ?: "Your video is ready"
        showNotification(title, body)
    }

    override fun onNewToken(token: String) {
        scope.launch {
            try {
                api.updateFcmToken(mapOf("token" to token))
            } catch (e: Exception) {
                // Not fatal: worst case is a missed push notification, retried
                // implicitly next time FCM rotates the token or the app restarts.
            }
        }
    }

    private fun showNotification(title: String, body: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Video generation", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: replace with app icon/notification icon asset
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
