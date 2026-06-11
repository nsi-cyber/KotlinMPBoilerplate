package com.nsicyber.composeboilerplatform.sharedlogic.core.audioplayer

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AudioRepositoryTest {

    @Test
    fun setQueue_startsFromRequestedIndex() = runBlocking {
        var fakeEngine: FakeAudioPlayerEngine? = null
        val repository = AudioRepository(
            playerEngineFactory = AudioPlayerEngineFactory { state, position, duration, buffered ->
                FakeAudioPlayerEngine(state, position, duration, buffered).also { fakeEngine = it }
            }
        )
        val tracks = sampleTracks()

        repository.setQueue(tracks = tracks, startIndex = 1)
        delay(100)

        assertEquals("2", repository.currentTrack.value?.id)
        assertEquals("https://example.com/2.mp3", fakeEngine?.lastLoadedUrl)
        repository.cleanup()
    }

    @Test
    fun endedState_autoAdvancesToNextTrack() = runBlocking {
        var fakeEngine: FakeAudioPlayerEngine? = null
        val repository = AudioRepository(
            playerEngineFactory = AudioPlayerEngineFactory { state, position, duration, buffered ->
                FakeAudioPlayerEngine(state, position, duration, buffered).also { fakeEngine = it }
            }
        )
        val tracks = sampleTracks()

        repository.setQueue(tracks = tracks, startIndex = 0)
        fakeEngine?.emitState(PlaybackState.ENDED)
        delay(100)

        assertEquals("2", repository.currentTrack.value?.id)
        repository.cleanup()
    }

    @Test
    fun next_skipsTracksWithoutPlayableUrl() = runBlocking {
        var fakeEngine: FakeAudioPlayerEngine? = null
        val repository = AudioRepository(
            playerEngineFactory = AudioPlayerEngineFactory { state, position, duration, buffered ->
                FakeAudioPlayerEngine(state, position, duration, buffered).also { fakeEngine = it }
            }
        )
        val tracks = listOf(
            PlayerTrack(id = "1", url = "https://example.com/1.mp3", title = "Track 1"),
            PlayerTrack(id = "2", url = null, title = "Track 2"),
            PlayerTrack(id = "3", url = "https://example.com/3.mp3", title = "Track 3")
        )

        repository.setQueue(tracks, startIndex = 0)
        repository.next()
        delay(100)

        assertEquals("3", repository.currentTrack.value?.id)
        assertEquals("https://example.com/3.mp3", fakeEngine?.lastLoadedUrl)
        repository.cleanup()
    }

    @Test
    fun setSpeed_clampsAndForwardsToEngine() {
        var fakeEngine: FakeAudioPlayerEngine? = null
        val repository = AudioRepository(
            playerEngineFactory = AudioPlayerEngineFactory { state, position, duration, buffered ->
                FakeAudioPlayerEngine(state, position, duration, buffered).also { fakeEngine = it }
            }
        )

        repository.setSpeed(5f)

        assertEquals(2f, repository.playbackSpeed.value)
        assertEquals(2f, fakeEngine?.lastSpeed)
        repository.cleanup()
    }

    @Test
    fun playSingle_withMissingUrl_emitsError() = runBlocking {
        val repository = AudioRepository(
            playerEngineFactory = AudioPlayerEngineFactory { state, position, duration, buffered ->
                FakeAudioPlayerEngine(state, position, duration, buffered)
            }
        )
        val missingTrack = PlayerTrack(id = "404", url = null, title = "Missing")

        val errorDeferred = async { withTimeout(1_000) { repository.errors.first() } }
        repository.playSingle(missingTrack)
        val emittedError = errorDeferred.await()

        assertEquals("404", emittedError.id)
        assertFalse(repository.isPlaying.value)
        repository.cleanup()
    }
}

private class FakeAudioPlayerEngine(
    private val stateFlow: MutableStateFlow<PlaybackState>,
    private val positionFlow: MutableStateFlow<Long>,
    private val durationFlow: MutableStateFlow<Long>,
    private val bufferedFlow: MutableStateFlow<Long>
) : AudioPlayerEngine {
    var lastLoadedUrl: String? = null
    var lastSpeed: Float = 1f
    private var initialized = false

    override fun initialize() {
        initialized = true
        stateFlow.value = PlaybackState.IDLE
    }

    override fun load(url: String) {
        lastLoadedUrl = url
        stateFlow.value = PlaybackState.PLAYING
        durationFlow.value = 120_000L
        positionFlow.value = 0L
        bufferedFlow.value = 30_000L
    }

    override fun play() {
        stateFlow.value = PlaybackState.PLAYING
    }

    override fun pause() {
        stateFlow.value = PlaybackState.PAUSED
    }

    override fun stop() {
        stateFlow.value = PlaybackState.IDLE
        positionFlow.value = 0L
        durationFlow.value = 0L
        bufferedFlow.value = 0L
    }

    override fun seekTo(positionMs: Long) {
        positionFlow.value = positionMs
    }

    override fun setPlaybackSpeed(speed: Float) {
        lastSpeed = speed
    }

    override fun release() {
        initialized = false
        stateFlow.value = PlaybackState.IDLE
    }

    fun emitState(state: PlaybackState) {
        assertTrue(initialized)
        stateFlow.value = state
    }
}

private fun sampleTracks(): List<PlayerTrack> = listOf(
    PlayerTrack(id = "1", url = "https://example.com/1.mp3", title = "Track 1"),
    PlayerTrack(id = "2", url = "https://example.com/2.mp3", title = "Track 2")
)
