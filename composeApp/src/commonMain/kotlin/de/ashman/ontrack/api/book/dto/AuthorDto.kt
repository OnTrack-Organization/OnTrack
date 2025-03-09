package de.ashman.ontrack.api.book.dto

import de.ashman.ontrack.api.book.dto.deserializer.BookSerializer
import de.ashman.ontrack.api.book.dto.deserializer.TypeSerializer
import kotlinx.serialization.Serializable

@Serializable
data class AuthorDto(
    val key: String = "",
    val name: String? = null,
    val birthDate: String? = null,
    val deathDate: String? = null,
    @Serializable(with = BookSerializer::class)
    val bio: String? = null,
    val photos: List<Int>? = null,
    val personalName: String? = null,
    val entityType: String? = null,
    val links: List<LinkDto>? = null,
    val remoteIds: RemoteIdsDto? = null,
    @Serializable(with = TypeSerializer::class)
    val type: String? = null,
    val title: String? = null,
    val fullerName: String? = null,
    val alternateNames: List<String>? = null,
    val sourceRecords: List<String>? = null,
    val latestRevision: Int? = null,
    val revision: Int? = null,
    val created: DateTimeDto? = null,
    val lastModified: DateTimeDto? = null
)

@Serializable
data class LinkDto(
    val title: String? = null,
    val url: String? = null,
    @Serializable(with = TypeSerializer::class)
    val type: String? = null,
)

@Serializable
data class RemoteIdsDto(
    val viaf: String? = null,
    val goodreads: String? = null,
    val storygraph: String? = null,
    val isni: String? = null,
    val librarything: String? = null,
    val amazon: String? = null,
    val wikidata: String? = null
)

@Serializable
data class DateTimeDto(
    val type: String? = null,
    val value: String? = null,
)
