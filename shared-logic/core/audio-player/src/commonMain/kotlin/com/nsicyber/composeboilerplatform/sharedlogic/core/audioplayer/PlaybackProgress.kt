package com.nsicyber.composeboilerplatform.sharedlogic.core.audioplayer

/**
 * Snapshot of timeline metrics emitted by [AudioRepository].
 */
data class PlaybackProgress(
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val speed: Float = 1f
)
