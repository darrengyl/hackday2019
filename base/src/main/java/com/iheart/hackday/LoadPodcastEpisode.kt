package com.iheart.hackday

import com.iheart.hackday.api.PodcastApi
import io.reactivex.Single

/**
 * Created by Jonathan Muller on 2/21/19.
 */
class LoadPodcastEpisode {

    operator fun invoke(episodeId: Long): Single<PodcastEpisode> {
        return PodcastApi().fetchEpisode(episodeId)
    }
}

data class PodcastEpisode(val id: Long,
                          val imageUrl: String,
                          val title: String,
                          val description: String,
                          val streamUrl: String)