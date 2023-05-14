package cat.copernic.roomdecision.selmeem

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * My firebase messaging service
 *
 * @constructor Create empty My firebase messaging service
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Obtenir el contingut de la notificació
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body

        // Crear la notificació
        val notificationBuilder = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.notificacio)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, notificationBuilder.build())
    }
}
