package com.iheart.hackday

import android.media.MediaPlayer
import android.util.Log

/**
 * Created by Jonathan Muller on 2/21/19.
 */

const val testUrl = "https://dts.podtrac.com/redirect.mp3/traffic.libsyn.com/secure/billburr/TAMMP_2-14-19.mp3?dest-id=102119"

class Player {

    private val mediaPlayer = MediaPlayer()

    fun setPodastStreamUrl(streamUrl: String) {
        mediaPlayer.setDataSource(streamUrl)
        mediaPlayer.prepareAsync()
    }

    fun play() {
        mediaPlayer.start()
    }

    fun isPlaying() = mediaPlayer.isPlaying

    fun stop() {
        mediaPlayer.pause()
    }

    fun seekTo(positionProgress: Int) {
        val newPosition = positionProgress * durationInMilliSec / 100
        if (newPosition < durationInMilliSec) {
            mediaPlayer.seekTo(newPosition)
        }
    }

    fun skipAhead30Secs() {
        val futureProgress = progressInMilliSec + 30000
        if (futureProgress < durationInMilliSec) {
            mediaPlayer.seekTo(futureProgress)
        }
    }

    fun goBack15Secds() {
        val pastProgress = progressInMilliSec - 15000
        if (pastProgress < durationInMilliSec) {
            mediaPlayer.seekTo(pastProgress)
        }
    }

    val durationInMilliSec: Int
        get() = mediaPlayer.duration

    val progressInMilliSec: Int
        get() = mediaPlayer.currentPosition

    val progress: Int
        get() = mediaPlayer.currentPosition * 100 / mediaPlayer.duration

}
