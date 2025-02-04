package de.ashman.ontrack.api.book

import de.ashman.ontrack.api.book.dto.AuthorDto
import de.ashman.ontrack.api.book.dto.BookDto
import de.ashman.ontrack.api.book.dto.BookWorksResponse
import de.ashman.ontrack.api.utils.cleanupAuthorKey
import de.ashman.ontrack.api.utils.cleanupBookKey
import de.ashman.ontrack.api.utils.cleanupDescription
import de.ashman.ontrack.api.utils.filterGenres
import de.ashman.ontrack.api.utils.formatCreatorDate
import de.ashman.ontrack.api.utils.getOpenLibraryCoverUrl
import de.ashman.ontrack.domain.Author
import de.ashman.ontrack.domain.Book

fun BookDto.toDomain(): Book =
    Book(
        id = key.cleanupBookKey(),
        title = title,
        coverUrl = coverI.getOpenLibraryCoverUrl(),
        releaseYear = firstPublishYear.toString(),
        numberOfPagesMedian = numberOfPagesMedian,
        publisher = publisher,
        apiRating = ratingsAverage,
        apiRatingCount = ratingsCount,
        genres = subject?.filterGenres(),
        author = Author(
            id = authorKey.first().cleanupAuthorKey(),
        )
    )

fun BookWorksResponse.toDomain(): Book =
    Book(
        id = key.cleanupBookKey(),
        title = title,
        coverUrl = covers?.firstOrNull()?.getOpenLibraryCoverUrl().orEmpty(),
        author = authors?.first()?.author?.toDomain() ?: Author(id = ""),
        apiRating = null,
        apiRatingCount = null,
    )

fun AuthorDto.toDomain(): Author =
    Author(
        id = key.cleanupAuthorKey(),
        name = name,
        imageUrl = photos?.firstOrNull()?.getOpenLibraryCoverUrl(),
        bio = bio?.cleanupDescription(),
        birthDate = birthDate?.formatCreatorDate(),
        deathDate = deathDate?.formatCreatorDate(),
    )
