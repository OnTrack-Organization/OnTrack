package de.ashman.ontrack.network.services.share.dto

import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: Sort? = null,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
)

@Serializable
data class Sort(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean
)
