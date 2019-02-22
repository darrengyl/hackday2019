package com.iheart.hackday.api

import android.os.Build
import com.iheart.hackday.PodcastEpisode
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


/**
 * Created by Jonathan Muller on 2/21/19.
 */


class PodcastApi {

    private val profileId = "981389662"
    private val sessionId = "YR2vWUfjsw4Ce4bfy6KzQn"

    val userAgent = StringBuilder().append("iHeartRadio/")
        .append(1.2)
        .append(" (Android ")
        .append(Build.VERSION.RELEASE)
        .append("; ")
        .append(Build.MODEL)
        .append(" Build/")
        .append(Build.ID)
        .append(")")
        .toString()

    val userAgentInterceptor : Interceptor = object : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val requestWithUserAgent = originalRequest.newBuilder()
                .header("User-Agent", userAgent)
                .build()
            return chain.proceed(requestWithUserAgent)
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(userAgentInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://us.api.iheart.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build()

    private val podcastService = retrofit.create(PodcastApiService::class.java)

    fun fetchEpisode(id: Long): Single<PodcastEpisode> {
        return podcastService.getEpisode(id, profileId, sessionId)
            .map { PodcastEpisode(
                id = it.episode!!.id!!.or(-1),
                imageUrl = createImageUrl(it.episode.id!!),
                title = it.episode.title.orEmpty(),
                description = it.episode.description.orEmpty(),
                streamUrl = it.episode.mediaUrl.orEmpty()) }
    }

    private fun createImageUrl(id: Long): String = "https://i.iheart.com/v3/catalog/episode/$id"
}