package com.nsicyber.composeboilerplatform.sharedlogic.core.audioplayer

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Platform engine abstraction used by the shared repository orchestration.
 */
interface AudioPlayerEngine {
    fun initialize()
    fun load(url: String)
    fun play()
    fun pause()
    fun stop()
    fun seekTo(positionMs: Long)
    fun setPlaybackSpeed(speed: Float)
    fun release()
}

fun interface AudioPlayerEngineFactory {
    fun create(
        playbackStateFlow: MutableStateFlow<PlaybackState>,
        positionMsFlow: MutableStateFlow<Long>,
        durationMsFlow: MutableStateFlow<Long>,
        bufferedPositionMsFlow: MutableStateFlow<Long>
    ): AudioPlayerEngine
}

/**
 * Creates the platform-specific engine implementation.
 */
expect fun createAudioPlayerEngine(
    playbackStateFlow: MutableStateFlow<PlaybackState>,
    positionMsFlow: MutableStateFlow<Long>,
    durationMsFlow: MutableStateFlow<Long>,
    bufferedPositionMsFlow: MutableStateFlow<Long>
): AudioPlayerEngine

/**
 * Initializes platform audio resources where host setup is required.
 *
 * Android hosts should pass `applicationContext`, while iOS can ignore this call.
 */
expect fun initializePlatformAudio(context: Any? = null)
