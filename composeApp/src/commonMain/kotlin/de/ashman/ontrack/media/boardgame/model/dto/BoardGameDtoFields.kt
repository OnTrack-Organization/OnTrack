package de.ashman.ontrack.media.boardgame.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@SerialName("name")
data class Name(
    val type: String,
    val sortindex: Int? = null,
    val value: String
)

@Serializable
@SerialName("yearpublished")
data class YearPublished(
    val value: String
)

@Serializable
@SerialName("minplayers")
data class MinPlayers(
    val value: String
)

@Serializable
@SerialName("maxplayers")
data class MaxPlayers(
    val value: String
)

@Serializable
@SerialName("playingtime")
data class PlayingTime(
    val value: String
)

@Serializable
@SerialName("minplaytime")
data class MinPlayTime(
    val value: String
)

@Serializable
@SerialName("maxplaytime")
data class MaxPlayTime(
    val value: String
)

@Serializable
@SerialName("minage")
data class MinAge(
    val value: String
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

@Serializable
@XmlSerialName("statistics")
data class StatisticsDto(
    val page: String,
    @XmlElement
    val ratings: RatingsDto
) {
    @Serializable
    @SerialName("ratings")
    data class RatingsDto(
        @XmlElement
        val usersRated: UsersRated,
        @XmlElement
        val average: Average,
        @XmlElement
        val bayesAverage: BayesAverage,
        @XmlElement
        val ranks: Ranks,
        @XmlElement
        val stdDev: StandardDeviation,
        @XmlElement
        val median: Median,
        @XmlElement
        val owned: Owned,
        @XmlElement
        val trading: Trading,
        @XmlElement
        val wanting: Wanting,
        @XmlElement
        val wishing: Wishing,
        @XmlElement
        val numComments: NumComments,
        @XmlElement
        val numWeights: NumWeights,
        @XmlElement
        val averageWeight: AverageWeight
    ) {
        @Serializable
        @SerialName("usersrated")
        data class UsersRated(
            val value: Int
        )

        @Serializable
        @SerialName("average")
        data class Average(
            val value: Double
        )

        @Serializable
        @SerialName("bayesaverage")
        data class BayesAverage(
            val value: Double
        )

        @Serializable
        @SerialName("ranks")
        data class Ranks(
            @XmlElement
            val rank: List<Rank>
        ) {
            @Serializable
            @SerialName("rank")
            data class Rank(
                val id: String,
                val type: String,
                val name: String,
                val friendlyname: String,
                val value: String,
                val bayesaverage: Double
            )
        }

        @Serializable
        @SerialName("stddev")
        data class StandardDeviation(
            val value: Double
        )

        @Serializable
        @SerialName("median")
        data class Median(
            val value: Int
        )

        @Serializable
        @SerialName("owned")
        data class Owned(
            val value: Int
        )

        @Serializable
        @SerialName("trading")
        data class Trading(
            val value: Int
        )

        @Serializable
        @SerialName("wanting")
        data class Wanting(
            val value: Int
        )

        @Serializable
        @SerialName("wishing")
        data class Wishing(
            val value: Int
        )

        @Serializable
        @SerialName("numcomments")
        data class NumComments(
            val value: Int
        )

        @Serializable
        @SerialName("numweights")
        data class NumWeights(
            val value: Int
        )

        @Serializable
        @SerialName("averageweight")
        data class AverageWeight(
            val value: Double
        )
    }
}