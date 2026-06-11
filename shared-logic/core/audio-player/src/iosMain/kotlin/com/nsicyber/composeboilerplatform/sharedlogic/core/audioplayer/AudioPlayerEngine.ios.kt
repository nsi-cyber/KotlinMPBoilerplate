@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.nsicyber.composeboilerplatform.sharedlogic.core.audioplayer

import kotlinx.coroutines.flow.MutableStateFlow
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.isPlaybackLikelyToKeepUp
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.NSEC_PER_SEC
import platform.darwin.dispatch_get_main_queue

actual fun initializePlatformAudio(context: Any?) = Unit

actual fun createAudioPlayerEngine(
    playbackStateFlow: MutableStateFlow<PlaybackState>,
    positionMsFlow: MutableStateFlow<Long>,
    durationMsFlow: MutableStateFlow<Long>,
    bufferedPositionMsFlow: MutableStateFlow<Long>
): AudioPlayerEngine = IOSAudioPlayerEngine(
    playbackStateFlow = playbackStateFlow,
    positionMsFlow = positionMsFlow,
    durationMsFlow = durationMsFlow,
    bufferedPositionMsFlow = bufferedPositionMsFlow
)

private class IOSAudioPlayerEngine(
    private val playbackStateFlow: MutableStateFlow<PlaybackState>,
    private val positionMsFlow: MutableStateFlow<Long>,
    private val durationMsFlow: MutableStateFlow<Long>,
    private val bufferedPositionMsFlow: MutableStateFlow<Long>
) : AudioPlayerEngine {
    private val avPlayer = AVPlayer()
    private var playbackSpeed: Float = 1f
    private var timeObserverToken: Any? = null
    private var endObserverToken: Any? = null

    override fun initialize() {
        configureAudioSession()
        startTimeObserver()
        playbackStateFlow.value = PlaybackState.IDLE
    }

    override fun load(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: run {
            playbackStateFlow.value = PlaybackState.ERROR
            return
        }

        val item = AVPlayerItem(uRL = nsUrl)
        avPlayer.replaceCurrentItemWithPlayerItem(item)
        observeSongEnd()
        avPlayer.play()
        avPlayer.rate = playbackSpeed
        playbackStateFlow.value = PlaybackState.BUFFERING
    }

    override fun play() {
        avPlayer.play()
        avPlayer.rate = playbackSpeed
        playbackStateFlow.value = PlaybackState.PLAYING
    }

    override fun pause() {
        avPlayer.pause()
        playbackStateFlow.value = PlaybackState.PAUSED
    }

    override fun stop() {
        avPlayer.pause()
        avPlayer.currentItem?.seekToTime(CMTimeMakeWithSeconds(0.0, NSEC_PER_SEC.toInt()))
        resetTimeline()
        playbackStateFlow.value = PlaybackState.IDLE
    }

    override fun seekTo(positionMs: Long) {
        val seconds = positionMs.coerceAtLeast(0L) / 1000.0
        avPlayer.currentItem?.seekToTime(CMTimeMakeWithSeconds(seconds, NSEC_PER_SEC.toInt()))
        positionMsFlow.value = positionMs.coerceAtLeast(0L)
    }

    override fun setPlaybackSpeed(speed: Float) {
        playbackSpeed = speed
        if (avPlayer.timeControlStatus == AVPlayerTimeControlStatusPlaying) {
            avPlayer.rate = speed
        }
    }

    override fun release() {
        removeObservers()
        avPlayer.pause()
        resetTimeline()
        playbackStateFlow.value = PlaybackState.IDLE
    }

    private fun configureAudioSession() {
        runCatching {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayback, null)
            session.setActive(true, null)
        }
    }

    private fun startTimeObserver() {
        if (timeObserverToken != null) return
        val interval = CMTimeMakeWithSeconds(0.5, NSEC_PER_SEC.toInt())
        timeObserverToken = avPlayer.addPeriodicTimeObserverForInterval(
            interval = interval,
            queue = dispatch_get_main_queue()
        ) { _ ->
            updateTimelineAndState()
        }
    }

    private fun observeSongEnd() {
        endObserverToken?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        endObserverToken = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = avPlayer.currentItem,
            queue = NSOperationQueue.mainQueue
        ) {
            playbackStateFlow.value = PlaybackState.ENDED
        }
    }

    private fun updateTimelineAndState() {
        val currentItem = avPlayer.currentItem
        val positionSeconds = CMTimeGetSeconds(avPlayer.currentTime())
        val durationSeconds = currentItem?.duration?.let { CMTimeGetSeconds(it) } ?: 0.0

        positionMsFlow.value = secondsToMillis(positionSeconds)
        durationMsFlow.value = secondsToMillis(durationSeconds)
        bufferedPositionMsFlow.value = positionMsFlow.value

        playbackStateFlow.value = when {
            avPlayer.timeControlStatus == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate ->
                PlaybackState.BUFFERING

            avPlayer.timeControlStatus == AVPlayerTimeControlStatusPlaying ->
                if (currentItem?.isPlaybackLikelyToKeepUp() == false) {
                    PlaybackState.BUFFERING
                } else {
                    PlaybackState.PLAYING
                }

            playbackStateFlow.value == PlaybackState.IDLE || playbackStateFlow.value == PlaybackState.ENDED ->
                playbackStateFlow.value

            else -> PlaybackState.PAUSED
        }
    }

    private fun removeObservers() {
        timeObserverToken?.let { avPlayer.removeTimeObserver(it) }
        timeObserverToken = null

        endObserverToken?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        endObserverToken = null
    }

    private fun secondsToMillis(seconds: Double): Long {
        if (seconds.isNaN() || seconds.isInfinite() || seconds < 0.0) return 0L
        return (seconds * 1000.0).toLong()
    }

    private fun resetTimeline() {
        positionMsFlow.value = 0L
        durationMsFlow.value = 0L
        bufferedPositionMsFlow.value = 0L
    }
}
