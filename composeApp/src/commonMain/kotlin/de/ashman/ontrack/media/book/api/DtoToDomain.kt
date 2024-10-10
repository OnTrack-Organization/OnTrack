package de.ashman.ontrack.media.book.api

import de.ashman.ontrack.media.model.Book
import de.ashman.ontrack.media.book.model.dto.BookDto
import de.ashman.ontrack.media.book.model.dto.BookWorksResponseDto
import de.ashman.ontrack.media.getOpenLibraryCoverUrl

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
