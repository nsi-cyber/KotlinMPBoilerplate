package com.nsicyber.composeboilerplatform.sharedlogic.core.audioplayer

/**
 * High-level playback states shared by Android and iOS engines.
 */
enum class PlaybackState {
    IDLE,
    BUFFERING,
    READY,
    PAUSED,
    PLAYING,
    ENDED,
    ERROR
}
