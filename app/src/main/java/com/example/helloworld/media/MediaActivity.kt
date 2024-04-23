package com.example.helloworld.media

import android.content.ComponentName
import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.example.helloworld.R
import com.example.helloworld.launchmode.BaseActivity
import com.google.common.util.concurrent.MoreExecutors

//https://developer.android.com/media/implement/playback-app?hl=zh-cn
class MediaActivity : BaseActivity() {


    lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_media_activity)
        playerView = findViewById(R.id.playerView)
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                // Call controllerFuture.get() to retrieve the MediaController.
                // MediaController implements the Player interface, so it can be
                // attached to the PlayerView UI component.
                playerView.player = controllerFuture.get()
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerView.player?.release()
    }
}