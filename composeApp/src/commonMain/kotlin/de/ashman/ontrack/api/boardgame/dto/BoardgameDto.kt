package de.ashman.ontrack.api.boardgame.dto

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("item")
data class BoardgameDto(
    val id: String,
    val type: String? = null,
    @XmlElement
    val image: String? = null,
    @XmlElement
    val thumbnail: String? = null,
    @XmlElement
    val names: List<Name>,
    @XmlElement
    val description: String? = null,
    @XmlElement
    val yearpublished: YearPublished? = null,
    @XmlElement
    val minplayers: MinPlayers? = null,
    @XmlElement
    val maxplayers: MaxPlayers? = null,
    @XmlElement
    val playingtime: PlayingTime? = null,
    @XmlElement
    val minplaytime: MinPlayTime? = null,
    @XmlElement
    val maxplaytime: MaxPlayTime? = null,
    @XmlElement
    val minage: MinAge? = null,
    @XmlElement
    val pollSummary: PollSummary? = null,
    @XmlElement
    val polls: List<Poll>? = null,
    @XmlElement
    val links: List<Link>? = null,
    @XmlElement
    val statistics: StatisticsDto? = null,
    val rank: String? = null,
)
