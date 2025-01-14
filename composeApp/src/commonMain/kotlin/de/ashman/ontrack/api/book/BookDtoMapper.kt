package de.ashman.ontrack.api.book

import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.api.book.dto.BookDto
import de.ashman.ontrack.api.book.dto.BookWorksResponseDto
import de.ashman.ontrack.api.getOpenLibraryCoverUrl

fun BookDto.toDomain(): Book {
    return Book(
        id = key,
        name = title,
        coverUrl = coverI.getOpenLibraryCoverUrl(),
        releaseYear = firstPublishYear.toString(),
        authorKeys = authorKey,
        authors = authorName,
        description = description,
        firstSentence = firstSentence,
        language = language,
        numberOfPagesMedian = numberOfPagesMedian,
        person = person,
        place = place,
        publisher = publisher,
        ratingsAverage = ratingsAverage,
        ratingsCount = ratingsCount,
        subjects = subject?.take(5),
    )
}

fun BookWorksResponseDto.toDomain(): Book {
    return Book(
        id = "",
        name = "",
        coverUrl = "",
        description = description,
        releaseYear = "",
    )
}
