package de.ashman.ontrack.music

data class Artist(
    val externalUrls: ExternalUrls,
    val followers: Followers,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val popularity: Int,
    val type: String,
    val uri: String
)

data class ExternalUrls(
    val spotify: String
)

data class Followers(
    val href: String?,
    val total: Int
)

data class Image(
    val height: Int,
    val url: String,
    val width: Int
)
