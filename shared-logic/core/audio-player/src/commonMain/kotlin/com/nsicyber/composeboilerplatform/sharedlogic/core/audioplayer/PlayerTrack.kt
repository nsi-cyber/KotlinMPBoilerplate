package com.nsicyber.composeboilerplatform.sharedlogic.core.audioplayer

/**
 * Portable track model used by the shared audio queue.
 */
data class PlayerTrack(
    val id: String,
    val url: String?,
    val title: String,
    val artistName: String? = null,
    val subtitle: String? = null,
    val artworkUrl: String? = null,
    val album: String? = null
)
