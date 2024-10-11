package de.ashman.ontrack.api.book

import de.ashman.ontrack.media.domain.Book
import de.ashman.ontrack.api.book.dto.BookDto
import de.ashman.ontrack.api.book.dto.BookWorksResponseDto
import de.ashman.ontrack.api.getOpenLibraryCoverUrl

fun BookDto.toDomain(): Book {
    return Book(
        id = key,
        name = title,
        coverUrl = coverI.getOpenLibraryCoverUrl(),
        authorKey = authorKey,
        authorName = authorName,
        description = description,
        firstPublishYear = firstPublishYear,
        firstSentence = firstSentence,
        language = language,
        numberOfPagesMedian = numberOfPagesMedian,
        person = person,
        place = place,
        publisher = publisher,
        ratingsAverage = ratingsAverage,
        ratingsCount = ratingsCount,
        subject = subject,
    )
}

// TODO change
fun BookWorksResponseDto.toDomain(): Book {
    return Book(
        id = "",
        name = "",
        coverUrl = "",
        description = description?.value,
    )
}
