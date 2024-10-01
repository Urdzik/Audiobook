package com.urdzik.feature.player.presentation

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MediaPlayerService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var currentTrackUrl: String? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            setOnCompletionListener {
                stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentTrackUrl = intent?.getStringExtra("audioUrl")

        if (!currentTrackUrl.isNullOrEmpty()) {
            playAudio(currentTrackUrl!!)
        }

        startForegroundService()
        return START_STICKY
    }

    private fun playAudio(url: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun createNotification(): Notification {
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this, "media_playback")
            .setContentTitle("Playing audio")
            .setContentText("Audio is playing in the background")
            .setSmallIcon(audiobook.R.drawable.ic_pause)
//            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}