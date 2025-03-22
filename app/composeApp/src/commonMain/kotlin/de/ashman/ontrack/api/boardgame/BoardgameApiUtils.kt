package de.ashman.ontrack.api.boardgame

import de.ashman.ontrack.api.boardgame.dto.BoardgameResponseDto
import nl.adaptivity.xmlutil.serialization.XML

fun convertXmlToResponse(xmlString: String): BoardgameResponseDto {
    val xml = XML { indentString = "  " }
    val response = xml.decodeFromString(BoardgameResponseDto.serializer(), xmlString)

    return if (response.boardgames.isEmpty() || response.boardgames.any { it.error != null }) {
        BoardgameResponseDto()
    } else {
        response
    }
}

fun String.decodeHtmlManually(): String {
    return this
        .replace(Regex("&uuml;"), "ü")
        .replace(Regex("&gt;"), ">")
        .replace(Regex("&lt;"), "<")
        .replace(Regex("&rsquo;"), "'")
        .replace(Regex("&nbsp;"), " ")
        .replace(Regex("&amp;#10;"), "\n")
        .replace(Regex("&#10;"), "\n")
        .replace(Regex("&ndash;"), "–")
        .replace(Regex("&mdash;"), "—")
        .replace(Regex("&amp;"), "&")
        .replace(Regex("&quot;"), "\"")
        .replace(Regex("&#9;"), "")
        .replace(Regex("—description from the publisher\\n{2}"), "")
        .trimEnd()
}

fun String.nonZeroToInt(): Int? = this.takeIf { it != "0" }?.toInt()

fun getBoardgameDetailUrl(id: String) = "https://boardgamegeek.com/boardgame/$id"