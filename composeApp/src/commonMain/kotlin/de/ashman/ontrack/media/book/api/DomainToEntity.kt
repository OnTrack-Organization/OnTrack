package de.ashman.ontrack.media.book.api

import de.ashman.ontrack.media.book.model.domain.Book
import de.ashman.ontrack.media.book.model.entity.BookEntity

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = id,
        name = name,
        coverUrl = coverUrl,
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
