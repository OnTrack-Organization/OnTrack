package de.ashman.ontrack.boardgame.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("item")
data class BoardGameDto(
    @SerialName("type")
    val type: String,

    @SerialName("id")
    val id: String,

    @XmlElement
    @SerialName("thumbnail")
    val thumbnail: String? = null,

    @XmlElement
    @SerialName("image")
    val image: String? = null,

    @XmlElement
    val name: Name,

    @XmlElement
    @SerialName("description")
    val description: String? = null,

    @XmlElement
    val yearPublished: YearPublished? = null,

    @XmlElement
    val minPlayers: MinPlayers? = null,

    @XmlElement
    val maxPlayers: MaxPlayers? = null,

    @XmlElement
    val playingTime: PlayingTime? = null,

    @XmlElement
    val minPlayTime: MinPlayTime? = null,

    @XmlElement
    val maxPlayTime: MaxPlayTime? = null,

    @XmlElement
    val minAge: MinAge? = null,

    @XmlElement
    val polls: List<Poll>? = null,

    @XmlElement
    val links: List<Link>? = null
) {
    @Serializable
    @SerialName("name")
    data class Name(
        @SerialName("type")
        val type: String,

        @SerialName("sortindex")
        val sortIndex: Int? = null,

        @SerialName("value")
        val value: String
    )

    @Serializable
    @SerialName("yearpublished")
    data class YearPublished(
        @SerialName("value")
        val value: String
    )

    @Serializable
    @SerialName("minplayers")
    data class MinPlayers(
        @SerialName("value")
        val value: Int
    )

    @Serializable
    @SerialName("maxplayers")
    data class MaxPlayers(
        @SerialName("value")
        val value: Int
    )

    @Serializable
    @SerialName("playingtime")
    data class PlayingTime(
        @SerialName("value")
        val value: Int
    )

    @Serializable
    @SerialName("minplaytime")
    data class MinPlayTime(
        @SerialName("value")
        val value: Int
    )

    @Serializable
    @SerialName("maxplaytime")
    data class MaxPlayTime(
        @SerialName("value")
        val value: Int
    )

    @Serializable
    @SerialName("minage")
    data class MinAge(
        @SerialName("value")
        val value: Int
    )

    @Serializable
    @SerialName("poll")
    data class Poll(
        @SerialName("name")
        val name: String,

        @SerialName("title")
        val title: String,

        @SerialName("totalvotes")
        val totalVotes: Int,

        @XmlElement
        val results: List<Result>
    ) {
        @Serializable
        @SerialName("results")
        data class Result(
            @SerialName("numplayers")
            val numPlayers: String?,

            @XmlElement
            val result: List<IndividualResult>
        ) {
            @Serializable
            @SerialName("result")
            data class IndividualResult(
                @SerialName("level")
                val level: String?,

                @SerialName("value")
                val value: String,

                @SerialName("numvotes")
                val numVotes: Int
            )
        }
    }

    @Serializable
    @SerialName("link")
    data class Link(
        @SerialName("type")
        val type: String,

        @SerialName("id")
        val id: String,

        @SerialName("value")
        val value: String,

        @SerialName("inbound")
        val inbound: Boolean?,
    )
}
