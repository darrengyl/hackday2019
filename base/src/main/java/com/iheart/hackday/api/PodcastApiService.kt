package com.iheart.hackday.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

// https://us.api.iheart.com/api/v3/podcast/episodes/{id}"

/**
 * Created by Jonathan Muller on 2/21/19.
 */
const val POWER_AMP_PROFILE_ID = "X-IHR-Profile-ID"
const val POWER_AMP_SESSION_ID = "X-IHR-Session-ID"

interface PodcastApiService {

    @GET("/api/v3/podcast/episodes/{id}")
    fun getEpisode(@Path("id") id: Long,
                   @Header(POWER_AMP_PROFILE_ID) profileId: String,
                   @Header(POWER_AMP_SESSION_ID) sessionId: String): Single<EpisodeResponse>
}
