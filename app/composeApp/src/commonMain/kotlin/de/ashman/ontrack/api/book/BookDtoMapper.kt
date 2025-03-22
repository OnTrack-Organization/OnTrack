package de.ashman.ontrack.api.book

import de.ashman.ontrack.api.book.dto.AuthorDto
import de.ashman.ontrack.api.book.dto.BookDto
import de.ashman.ontrack.api.book.dto.BookWorksResponse
import de.ashman.ontrack.api.utils.formatDates
import de.ashman.ontrack.domain.media.Author
import de.ashman.ontrack.domain.media.Book

fun BookDto.toDomain(): Book =
    Book(
        id = key.cleanupBookKey(),
        title = title,
        coverUrl = coverI.getOpenLibraryCoverUrl(),
        detailUrl = getBookDetailUrl(key.cleanupBookKey()),
        releaseYear = firstPublishYear.toString(),
        numberOfPages = numberOfPagesMedian,
        publisher = publisher,
        genres = subject?.filterGenres(),
        author = Author(
            id = authorKey?.first()?.cleanupAuthorKey().orEmpty(),
        )
    )

fun BookWorksResponse.toDomain(): Book =
    Book(
        id = key.cleanupBookKey(),
        title = title,
        coverUrl = covers?.firstOrNull()?.getOpenLibraryCoverUrl(),
        author = authors?.first()?.author?.toDomain(),
        description = description?.cleanupDescription(),
        detailUrl = getBookDetailUrl(key.cleanupBookKey()),
    )

fun AuthorDto.toDomain(): Author =
    Author(
        id = key.cleanupAuthorKey(),
        name = name,
        imageUrl = photos?.firstOrNull()?.getOpenLibraryCoverUrl(),
        bio = bio?.cleanupDescription(),
        birthDate = birthDate?.formatDates(),
        deathDate = deathDate?.formatDates(),
    )
