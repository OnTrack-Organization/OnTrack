package de.ashman.ontrack.api.movie.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreditsDto(
    val cast: List<CastMemberDto>?,
    val crew: List<CrewMemberDto>?
)

@Serializable
data class CastMemberDto(
    val id: Int?,
    val name: String?,
    val character: String?,
    val order: Int?,
    val gender: Int?,
    val popularity: Double?,
    val profile_path: String?,
)

@Serializable
data class CrewMemberDto(
    val id: Int?,
    val name: String?,
    val job: String?,
    val department: String?,
    val gender: Int?,
    val popularity: Double?,
    val profile_path: String?,
)

@Serializable
data class PersonDetailsDto(
    val id: Int?,
    val name: String?,
    val also_known_as: List<String>?,
    val biography: String?,
    val birthday: String?,
    val deathday: String?,
    val gender: Int?,
    val place_of_birth: String?,
    val popularity: Double?,
    val profile_path: String?,
    val imdb_id: String?,
    val known_for_department: String?,
    val homepage: String?,
)
