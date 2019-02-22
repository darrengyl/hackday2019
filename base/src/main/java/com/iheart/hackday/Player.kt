package com.iheart.hackday

import android.media.MediaPlayer

/**
 * Created by Jonathan Muller on 2/21/19.
 */

const val testUrl = "https://dts.podtrac.com/redirect.mp3/traffic.libsyn.com/secure/billburr/TAMMP_2-14-19.mp3?dest-id=102119"

class Player {

    private var isInitialized = false

    private val mediaPlayer = MediaPlayer()

    fun setPodastStreamUrl(streamUrl: String) {
        if (!isInitialized) {
            mediaPlayer.setDataSource(streamUrl)
            mediaPlayer.prepare()
            isInitialized = true
        }
    }

    fun play() {
        mediaPlayer.start()
    }

    fun stop() {
        mediaPlayer.stop()
    }

    val duration: Int
        get() = mediaPlayer.duration / 1000 / 60

    val progress: Int
        get() = mediaPlayer.currentPosition / 1000 / 60

}
