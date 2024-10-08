package de.ashman.ontrack.media.book.api

import de.ashman.ontrack.media.book.model.domain.Book
import de.ashman.ontrack.media.book.model.dto.BookDto
import de.ashman.ontrack.media.book.model.dto.BookWorksResponseDto
import de.ashman.ontrack.media.book.model.dto.getImageUrl

fun BookDto.toDomain(): Book {
    return Book(
        id = key,
        authorKey = authorKey,
        authorName = authorName,
        coverUrl = coverI?.getImageUrl(),
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
        title = title
    )
}

fun BookWorksResponseDto.toDomain() : Book {
    return Book(
        id = "",
        description = description?.value,
    )
}
