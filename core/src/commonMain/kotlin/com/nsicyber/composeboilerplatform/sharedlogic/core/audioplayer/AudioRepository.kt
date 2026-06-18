package com.nsicyber.composeboilerplatform.sharedlogic.core.audioplayer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Ready-to-use audio orchestration layer:
 * - manages queue and current index
 * - delegates playback operations to platform engines
 * - exposes reactive playback/timeline state for consumers
 */
class AudioRepository(
    playerEngineFactory: AudioPlayerEngineFactory = AudioPlayerEngineFactory(::createAudioPlayerEngine)
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val defaultProgress = PlaybackProgress()

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    private val _positionMs = MutableStateFlow(0L)
    private val _durationMs = MutableStateFlow(0L)
    private val _bufferedPositionMs = MutableStateFlow(0L)
    private val _playbackSpeed = MutableStateFlow(1f)

    private val _queue = MutableStateFlow<List<PlayerTrack>>(emptyList())
    private val _currentIndex = MutableStateFlow(-1)
    private val _errors = MutableSharedFlow<PlayerTrack>(replay = 1, extraBufferCapacity = 1)

    private var isSingleMode = false
    private var isReleased = false

    private val playerEngine = playerEngineFactory.create(
        playbackStateFlow = _playbackState,
        positionMsFlow = _positionMs,
        durationMsFlow = _durationMs,
        bufferedPositionMsFlow = _bufferedPositionMs
    )

    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    val queue: StateFlow<List<PlayerTrack>> = _queue.asStateFlow()
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()
    val bufferedPositionMs: StateFlow<Long> = _bufferedPositionMs.asStateFlow()
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()
    val errors: SharedFlow<PlayerTrack> = _errors.asSharedFlow()

    val currentTrack: StateFlow<PlayerTrack?> = combine(_queue, _currentIndex) { tracks, index ->
        tracks.getOrNull(index)
    }.stateIn(scope, SharingStarted.Eagerly, null)

    val isPlaying: StateFlow<Boolean> = playbackState
        .map { state -> state == PlaybackState.PLAYING }
        .stateIn(scope, SharingStarted.Eagerly, false)

    val isBuffering: StateFlow<Boolean> = playbackState
        .map { state -> state == PlaybackState.BUFFERING }
        .stateIn(scope, SharingStarted.Eagerly, false)

    val hasNext: StateFlow<Boolean> = combine(_queue, _currentIndex) { tracks, index ->
        (index + 1 until tracks.size).any { tracks[it].url != null }
    }.stateIn(scope, SharingStarted.Eagerly, false)

    val hasPrevious: StateFlow<Boolean> = combine(_queue, _currentIndex) { tracks, index ->
        (index - 1 downTo 0).any { tracks[it].url != null }
    }.stateIn(scope, SharingStarted.Eagerly, false)

    val progress: StateFlow<PlaybackProgress> = combine(
        _positionMs,
        _durationMs,
        _bufferedPositionMs,
        _playbackSpeed
    ) { positionMs, durationMs, bufferedPositionMs, speed ->
        PlaybackProgress(
            positionMs = positionMs,
            durationMs = durationMs,
            bufferedPositionMs = bufferedPositionMs,
            speed = speed
        )
    }.stateIn(scope, SharingStarted.Eagerly, defaultProgress)

    init {
        playerEngine.initialize()

        // Auto-advance only when queue mode is active.
        scope.launch {
            playbackState.collect { state ->
                if (state == PlaybackState.ENDED && !isSingleMode) {
                    next()
                }
            }
        }
    }

    fun setQueue(tracks: List<PlayerTrack>, startIndex: Int = 0) {
        if (isReleased) return
        isSingleMode = false
        _queue.value = tracks

        if (tracks.isEmpty()) {
            _currentIndex.value = -1
            stop()
            return
        }

        _currentIndex.value = startIndex.coerceIn(0, tracks.size - 1)
        playCurrentTrack()
    }

    fun setQueue(tracks: List<PlayerTrack>, startTrackId: String) {
        if (isReleased) return
        isSingleMode = false
        _queue.value = tracks

        if (tracks.isEmpty()) {
            _currentIndex.value = -1
            stop()
            return
        }

        val index = tracks.indexOfFirst { track -> track.id == startTrackId }
        if (index == -1) {
            _currentIndex.value = -1
            stop()
            return
        }

        _currentIndex.value = index
        playCurrentTrack()
    }

    fun playAt(index: Int) {
        if (isReleased) return
        if (index !in _queue.value.indices) return

        isSingleMode = false
        _currentIndex.value = index
        playCurrentTrack()
    }

    fun playAt(trackId: String): Boolean {
        if (isReleased) return false
        val track = _queue.value.firstOrNull { it.id == trackId } ?: return false
        if (track.url == null) {
            _errors.tryEmit(track)
            return false
        }

        isSingleMode = false
        _currentIndex.value = _queue.value.indexOf(track)
        playCurrentTrack()
        return true
    }

    fun playSingle(track: PlayerTrack) {
        if (isReleased) return
        if (track.url == null) {
            _errors.tryEmit(track)
            return
        }

        isSingleMode = true
        _queue.value = listOf(track)
        _currentIndex.value = 0
        playCurrentTrack()
    }

    fun play() {
        if (isReleased) return
        playerEngine.play()
    }

    fun pause() {
        if (isReleased) return
        playerEngine.pause()
    }

    fun stop() {
        if (isReleased) return
        playerEngine.stop()
        resetTimeline()
        _playbackState.value = PlaybackState.IDLE
    }

    fun togglePlayPause() {
        if (isReleased) return
        when (playbackState.value) {
            PlaybackState.PLAYING -> pause()
            PlaybackState.READY,
            PlaybackState.PAUSED -> play()
            else -> playCurrentTrack()
        }
    }

    fun seekTo(positionMs: Long) {
        if (isReleased) return
        val safePosition = positionMs.coerceAtLeast(0L)
        playerEngine.seekTo(safePosition)
        _positionMs.value = safePosition
    }

    fun setSpeed(speed: Float) {
        if (isReleased) return
        val safeSpeed = speed.coerceIn(0.5f, 2f)
        _playbackSpeed.value = safeSpeed
        playerEngine.setPlaybackSpeed(safeSpeed)
    }

    fun next() {
        if (isReleased) return
        val tracks = _queue.value
        val index = _currentIndex.value
        val nextIndex = (index + 1 until tracks.size).firstOrNull { tracks[it].url != null } ?: return

        _currentIndex.value = nextIndex
        playCurrentTrack()
    }

    fun previous() {
        if (isReleased) return
        val tracks = _queue.value
        val index = _currentIndex.value
        val previousIndex = (index - 1 downTo 0).firstOrNull { tracks[it].url != null } ?: return

        _currentIndex.value = previousIndex
        playCurrentTrack()
    }

    fun clearQueue() {
        if (isReleased) return
        playerEngine.stop()
        _queue.value = emptyList()
        _currentIndex.value = -1
        _playbackState.value = PlaybackState.IDLE
        resetTimeline()
        isSingleMode = false
    }

    fun cleanup() {
        if (isReleased) return
        isReleased = true
        playerEngine.release()
        _queue.value = emptyList()
        _currentIndex.value = -1
        _playbackState.value = PlaybackState.IDLE
        resetTimeline()
        scope.cancel()
    }

    private fun playCurrentTrack() {
        val track = _queue.value.getOrNull(_currentIndex.value)
        val url = track?.url
        if (url == null) {
            track?.let { _errors.tryEmit(it) }
            next()
            return
        }

        resetTimeline()
        playerEngine.load(url)
        playerEngine.setPlaybackSpeed(_playbackSpeed.value)
    }

    private fun resetTimeline() {
        _positionMs.value = 0L
        _durationMs.value = 0L
        _bufferedPositionMs.value = 0L
    }
}
