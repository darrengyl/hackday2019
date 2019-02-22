package com.iheart.hackday.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Jonathan Muller on 2/21/19.
 */
class EpisodeResponse : Serializable {

    @SerializedName("episode")
    val episode: Episode? = null

    companion object {

        private const val serialVersionUID = -6307937688853281858L
    }

}

class Episode(
    @field:SerializedName("id")
    val id: Long?,
    @field:SerializedName("podcastId")
    val podcastId: Long?,
    @field:SerializedName("podcastSlug")
    val podcastSlug: String?,
    @field:SerializedName("title")
    val title: String?,
    @field:SerializedName("duration")
    val duration: Int?,
    @field:SerializedName("secondsPlayed")
    val progress: Int?,
    @field:SerializedName("isExplicit")
    val isExplicit: Boolean?,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("image")
    val image: String?,
    @field:SerializedName("mediaUrl")
    val mediaUrl: String?,
    @field:SerializedName("startDate")
    val startDate: Long?,
    @field:SerializedName("endDate")
    val endDate: Long?,
    @field:SerializedName("ingestionDate")
    val ingestionDate: Long?
) : Serializable {
    companion object {

        private const val serialVersionUID = -6848402541531921656L
    }
}