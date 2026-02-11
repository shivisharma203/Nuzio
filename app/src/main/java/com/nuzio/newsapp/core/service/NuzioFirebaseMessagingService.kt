package com.nuzio.newsapp.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nuzio.newsapp.MainActivity
import com.nuzio.newsapp.R
import com.nuzio.newsapp.domain.model.NotificationData
import com.nuzio.newsapp.domain.model.NotificationPreference
import com.nuzio.newsapp.domain.model.NotificationType
import com.nuzio.newsapp.domain.usecase.notifications.GetNotificationPreferencesUseCase
import com.nuzio.newsapp.domain.usecase.notifications.SaveNotificationUseCase

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Firebase Cloud Messaging Service for handling push notifications
 */
@AndroidEntryPoint
class NuzioFirebaseMessagingService : FirebaseMessagingService() {
    
    @Inject
    lateinit var saveNotificationUseCase: SaveNotificationUseCase
    
    @Inject
    lateinit var getNotificationPreferencesUseCase: GetNotificationPreferencesUseCase
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    companion object {
        private const val CHANNEL_ID_BREAKING_NEWS = "breaking_news"
        private const val CHANNEL_ID_GENERAL = "general_notifications"
        private const val CHANNEL_ID_SECTION = "section_notifications"
        
        private const val NOTIFICATION_ID_BREAKING = 1000
        private const val NOTIFICATION_ID_GENERAL = 2000
        private const val NOTIFICATION_ID_SECTION = 3000
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
    
    /**
     * Called when a new FCM token is generated
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("New FCM token: $token")
        
        // TODO: Send token to your backend server if needed
        // For now, we just log it
        serviceScope.launch {
            // You can save the token or send it to your backend
            Timber.d("FCM Token refreshed: $token")
        }
    }
    
    /**
     * Called when a message is received
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Timber.d("Message received from: ${remoteMessage.from}")
        
        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage)
        }
        
        // Check if message contains notification payload
        remoteMessage.notification?.let { notification ->
            Timber.d("Message notification: ${notification.title}")
            handleNotificationMessage(remoteMessage)
        }
    }
    
    /**
     * Handle data message payload
     */
    private fun handleDataMessage(remoteMessage: RemoteMessage) {
        serviceScope.launch {
            try {
                // Get user preferences
                val preferences = getNotificationPreferencesUseCase().first()
                
                // Check if notifications are enabled
                if (!preferences.notificationsEnabled) {
                    Timber.d("Notifications are disabled, skipping")
                    return@launch
                }
                
                // Create notification data from FCM payload
                val notificationData = NotificationData.fromFcmData(
                    messageId = remoteMessage.messageId ?: System.currentTimeMillis().toString(),
                    data = remoteMessage.data
                )
                
                // Save to database
                saveNotificationUseCase(notificationData)
                
                // Show notification based on preferences
                if (shouldShowNotification(notificationData, preferences)) {
                    showNotification(notificationData, preferences)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling data message")
            }
        }
    }
    
    /**
     * Handle notification message payload
     */
    private fun handleNotificationMessage(remoteMessage: RemoteMessage) {
        serviceScope.launch {
            try {
                val preferences = getNotificationPreferencesUseCase().first()
                
                if (!preferences.notificationsEnabled) {
                    Timber.d("Notifications are disabled, skipping")
                    return@launch
                }
                
                // Extract notification data
                val notification = remoteMessage.notification ?: return@launch
                val data = remoteMessage.data
                
                val notificationData = NotificationData.fromFcmData(
                    messageId = remoteMessage.messageId ?: System.currentTimeMillis().toString(),
                    data = data + mapOf(
                        "title" to (notification.title ?: ""),
                        "message" to (notification.body ?: ""),
                        "image_url" to (notification.imageUrl?.toString() ?: "")
                    )
                )
                
                // Save to database
                saveNotificationUseCase(notificationData)
                
                // Show notification
                if (shouldShowNotification(notificationData, preferences)) {
                    showNotification(notificationData, preferences)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling notification message")
            }
        }
    }
    
    /**
     * Check if notification should be shown based on preferences
     */
    private fun shouldShowNotification(
        notification: NotificationData,
        preferences: NotificationPreference
    ): Boolean {
        return when (notification.type) {
           NotificationType.BREAKING_NEWS ->
                preferences.breakingNewsEnabled
            NotificationType.SECTION_SPECIFIC ->
                preferences.sectionNotificationsEnabled && 
                (notification.section?.let { preferences.isSectionEnabled(it) } ?: false)
            else -> true
        }
    }
    
    /**
     * Show notification to user
     */
    private fun showNotification(
        notification: NotificationData,
        preferences: NotificationPreference
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create pending intent for notification tap
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_id", notification.id)
            putExtra("article_url", notification.articleUrl)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            notification.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Determine channel and notification ID
        val (channelId, notificationId) = when (notification.type) {
NotificationType.BREAKING_NEWS ->
                CHANNEL_ID_BREAKING_NEWS to NOTIFICATION_ID_BREAKING
          NotificationType.SECTION_SPECIFIC ->
                CHANNEL_ID_SECTION to NOTIFICATION_ID_SECTION
            else -> 
                CHANNEL_ID_GENERAL to NOTIFICATION_ID_GENERAL
        }
        
        // Build notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // You'll need to add this icon
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        
        // Add sound if enabled
        if (preferences.soundEnabled) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }
        
        // Add vibration if enabled
        if (preferences.vibrationEnabled) {
            notificationBuilder.setVibrate(longArrayOf(0, 500, 250, 500))
        }
        
        // Add big text style for longer messages
        if (notification.message.length > 50) {
            notificationBuilder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notification.message)
            )
        }
        
        // Show notification
        notificationManager.notify(
            notificationId + notification.id.hashCode() % 1000,
            notificationBuilder.build()
        )
    }
    
    /**
     * Create notification channels for Android O and above
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Breaking News Channel
            val breakingChannel = NotificationChannel(
                CHANNEL_ID_BREAKING_NEWS,
                "Breaking News",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent breaking news alerts"
                enableVibration(true)
                enableLights(true)
            }
            
            // General News Channel
            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "General News",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General news updates"
            }
            
            // Section-specific Channel
            val sectionChannel = NotificationChannel(
                CHANNEL_ID_SECTION,
                "Section News",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "News from your favorite sections"
            }
            
            notificationManager.createNotificationChannels(
                listOf(breakingChannel, generalChannel, sectionChannel)
            )
        }
    }
}
