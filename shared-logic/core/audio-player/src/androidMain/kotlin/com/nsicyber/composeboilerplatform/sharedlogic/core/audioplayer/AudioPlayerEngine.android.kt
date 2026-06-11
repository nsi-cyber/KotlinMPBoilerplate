package com.nsicyber.composeboilerplatform.sharedlogic.core.audioplayer

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow

private object AndroidAudioContextHolder {
    var appContext: Context? = null
}

actual fun initializePlatformAudio(context: Any?) {
    val androidContext = context as? Context ?: return
    AndroidAudioContextHolder.appContext = androidContext.applicationContext
}

actual fun createAudioPlayerEngine(
    playbackStateFlow: MutableStateFlow<PlaybackState>,
    positionMsFlow: MutableStateFlow<Long>,
    durationMsFlow: MutableStateFlow<Long>,
    bufferedPositionMsFlow: MutableStateFlow<Long>
): AudioPlayerEngine {
    val appContext = AndroidAudioContextHolder.appContext
        ?: error("initializePlatformAudio(applicationContext) must be called before using AudioRepository on Android.")
    return AndroidAudioPlayerEngine(
        appContext = appContext,
        playbackStateFlow = playbackStateFlow,
        positionMsFlow = positionMsFlow,
        durationMsFlow = durationMsFlow,
        bufferedPositionMsFlow = bufferedPositionMsFlow
    )
}

private class AndroidAudioPlayerEngine(
    private val appContext: Context,
    private val playbackStateFlow: MutableStateFlow<PlaybackState>,
    private val positionMsFlow: MutableStateFlow<Long>,
    private val durationMsFlow: MutableStateFlow<Long>,
    private val bufferedPositionMsFlow: MutableStateFlow<Long>
) : AudioPlayerEngine {
    private val handler = Handler(Looper.getMainLooper())
    private var player: ExoPlayer? = null

    private val progressUpdater = object : Runnable {
        override fun run() {
            updateTimelineMetrics()
            handler.postDelayed(this, 500L)
        }
    }

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            playbackStateFlow.value = when (playbackState) {
                Player.STATE_IDLE -> PlaybackState.IDLE
                Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                Player.STATE_READY -> PlaybackState.READY
                Player.STATE_ENDED -> PlaybackState.ENDED
                else -> PlaybackState.ERROR
            }
            updateTimelineMetrics()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                playbackStateFlow.value = PlaybackState.PLAYING
            } else if (player?.playbackState == Player.STATE_READY) {
                playbackStateFlow.value = PlaybackState.PAUSED
            }
            updateTimelineMetrics()
        }

        override fun onPlayerError(error: PlaybackException) {
            playbackStateFlow.value = PlaybackState.ERROR
        }
    }

    override fun initialize() {
        handler.post {
            getOrCreatePlayer()
            handler.removeCallbacks(progressUpdater)
            handler.post(progressUpdater)
        }
    }

    override fun load(url: String) {
        handler.post {
            val exoPlayer = getOrCreatePlayer()
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
            exoPlayer.play()
            playbackStateFlow.value = PlaybackState.BUFFERING
        }
    }

    override fun play() {
        handler.post {
            player?.play()
            playbackStateFlow.value = PlaybackState.PLAYING
        }
    }

    override fun pause() {
        handler.post {
            player?.pause()
            playbackStateFlow.value = PlaybackState.PAUSED
        }
    }

    override fun stop() {
        handler.post {
            player?.stop()
            resetTimeline()
            playbackStateFlow.value = PlaybackState.IDLE
        }
    }

    override fun seekTo(positionMs: Long) {
        handler.post {
            player?.seekTo(positionMs)
            positionMsFlow.value = positionMs
        }
    }

    override fun setPlaybackSpeed(speed: Float) {
        handler.post {
            player?.playbackParameters = PlaybackParameters(speed)
        }
    }

    override fun release() {
        handler.post {
            handler.removeCallbacks(progressUpdater)
            player?.removeListener(listener)
            player?.release()
            player = null
            resetTimeline()
            playbackStateFlow.value = PlaybackState.IDLE
        }
    }

    private fun getOrCreatePlayer(): ExoPlayer {
        return player ?: ExoPlayer.Builder(appContext).build().also { created ->
            created.addListener(listener)
            player = created
        }
    }

    private fun updateTimelineMetrics() {
        val exoPlayer = player ?: return
        positionMsFlow.value = exoPlayer.currentPosition.coerceAtLeast(0L)
        durationMsFlow.value = exoPlayer.duration.takeIf { it > 0L } ?: 0L
        bufferedPositionMsFlow.value = exoPlayer.bufferedPosition.coerceAtLeast(0L)
    }

    private fun resetTimeline() {
        positionMsFlow.value = 0L
        durationMsFlow.value = 0L
        bufferedPositionMsFlow.value = 0L
    }
}
