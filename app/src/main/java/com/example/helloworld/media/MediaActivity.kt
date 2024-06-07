package com.example.helloworld.media

import android.annotation.SuppressLint
import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.example.helloworld.R
import com.example.helloworld.launchmode.BaseActivity
import com.google.common.util.concurrent.MoreExecutors

//https://developer.android.com/media/implement/playback-app?hl=zh-cn
class MediaActivity : BaseActivity() {

    lateinit var btnPlay: Button
    lateinit var btnLoopPlay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_media_activity)

        btnPlay = findViewById(R.id.btnPlay)
        btnPlay.setOnClickListener {
            initPlayer()
        }
        btnLoopPlay = findViewById(R.id.btnLoopPlay)
        btnLoopPlay.setOnClickListener {
            val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
            val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
            controllerFuture.addListener(
                {
                    // Call controllerFuture.get() to retrieve the MediaController.
                    // MediaController implements the Player interface, so it can be
                    // attached to the PlayerView UI component.
                    val mediaController = controllerFuture.get()
//                    mediaController.sendCustomCommand()
                },
                MoreExecutors.directExecutor()
            )
        }

    }

    override fun onStart() {
        super.onStart()

    }

    private fun initPlayer() {
        Log.d(PlaybackService.TAG, "initPlayer ")
        val mediaItem = MediaItem.Builder()
            .setMediaId("test_sound")
            .setUri(Uri.parse("asset:///test_sound.mp3"))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtist("DIF")
                    .setTitle("Muyu4E")
                    .setArtworkUri(Uri.parse("https://s.momocdn.com/s1/u/ehbehgfg/Mokugyo.jpg"))
                    .build()
            )
            .build()
        val player = ExoPlayer.Builder(this)
            .build()
        player.setMediaItem(mediaItem)
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d(PlaybackService.TAG, "onPlaybackStateChanged: ${playbackState}")
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    player.release()
                }
            }
        })
        player.playWhenReady = true
        player.prepare()
    }

}