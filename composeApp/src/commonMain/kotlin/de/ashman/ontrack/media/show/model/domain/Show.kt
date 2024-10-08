package de.ashman.ontrack.media.show.model.domain

data class Show(
    val id: String,
    val backdropPath: String?,
    val episodeRunTime: List<Int>?,
    val firstAirDate: String?,
    val genres: List<String>?,
    val languages: List<String>?,
    val name: String?,
    val numberOfEpisodes: Int?,
    val numberOfSeasons: Int?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalName: String?,
    val overview: String?,
    val popularity: Double?,
    val posterPath: String?,
    val seasons: List<Season>?,
    val status: String?,
    val voteAverage: Double?,
    val voteCount: Int?
)

data class Season(
    val id: Int,
    val airDate: String?,
    val episodeCount: Int?,
    val name: String?,
    val overview: String?,
    val posterPath: String?,
    val seasonNumber: Int?,
    val voteAverage: Double?
)
