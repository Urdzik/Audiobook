package com.urdzik.feature.player.domain.manager

import com.urdzik.feature.player.data.model.Chapter
import kotlinx.coroutines.flow.StateFlow

interface AudioManager {

    val mediaState: StateFlow<AudioState>

    fun addMediaItems(audios: List<Chapter>, imageUrl: String)

    fun play(mediaItemIndex: Int)

    fun resume()

    fun pause()

    fun getCurrentPosition(): Long

    fun destroy()

    fun skipToNextAudio()

    fun skipToPreviousAudio()

    fun getCurrentAudio(): Chapter?

    fun seekTo(position: Long)

    fun changeSpeed(speed: Float)

    fun getCurrentMediaItemIndex(): Int

    fun getMediaItemCount(): Int

    fun changeChapter(index: Int)
}