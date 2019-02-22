package com.iheart.hackday

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Jonathan Muller on 2/21/19.
 */

private val deeplink =
    "https://www.iheart.com/podcast/299-monday-morning-podcast-27975223/episode/thursday-afternoon-monday-morning-podcast-2-14-19-30564882?cmp=android_share"
private val url =
    "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"

class PodcastViewModel : ViewModel() {

    private val player = Player()

    private val regex = Regex("-(\\d{2,})\\w+")

    private val podcastEpisodeSubject = BehaviorSubject.create<PodcastEpisode>()
    //private val isPlayingSubject = BehaviorSubject.create<Boolean>()

    private var disposable: Disposable? = null

    fun setIntent(uri: Uri?): Single<PodcastEpisode> {
        return LoadPodcastEpisode().invoke(getIdFromIntent(uri))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess{ player.setPodastStreamUrl(it.streamUrl) }
    }

    //fun podcastInfoObservable(): Observable<PodcastEpisode> = podcastEpisodeSubject

    fun isPlaying(): Boolean = player.isPlaying()

    fun play() {
        player.play()
    }

    fun stop() {
        player.stop()
    }

    fun seekTo(progress: Int) {
        player.seekTo(progress)
    }

    fun getProgress() = player.progress

    fun skipAhead30Secs() {
        player.skipAhead30Secs()
    }

    fun goBack15Secds() {
        player.goBack15Secds()
    }

    private fun getIdFromIntent(uri: Uri?): Long {
        return (uri ?: Uri.parse(deeplink))
            ?.let { regex.find(it.lastPathSegment) }
            ?.value.toString().substring(1)
            .toLong()
    }

    override fun onCleared() {
        stop()
    }
}