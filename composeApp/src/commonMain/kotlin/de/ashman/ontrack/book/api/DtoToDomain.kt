package de.ashman.ontrack.book.api

import de.ashman.ontrack.book.model.Book
import de.ashman.ontrack.book.model.BookDto
import de.ashman.ontrack.book.model.Author
import de.ashman.ontrack.book.model.AuthorDto
import de.ashman.ontrack.book.model.BookAvailability
import de.ashman.ontrack.book.model.BookAvailabilityDto

fun BookDto.toDomain(): Book {
    return Book(
        key = key,
        title = title,
        editionCount = editionCount,
        coverId = coverId,
        coverEditionKey = coverEditionKey,
        subjects = subjects,
        iaCollection = iaCollection,
        lendingLibrary = lendingLibrary,
        printDisabled = printDisabled,
        authors = authors?.map { it.toDomain() },
        firstPublishYear = firstPublishYear,
        availability = availability?.toDomain()
    )
}

fun AuthorDto.toDomain(): Author {
    return Author(
        key = key,
        name = name
    )
}

fun BookAvailabilityDto.toDomain(): BookAvailability {
    return BookAvailability(
        status = status,
        availableToBrowse = availableToBrowse,
        availableToBorrow = availableToBorrow,
        availableToWaitlist = availableToWaitlist,
        isPrintDisabled = isPrintDisabled,
        isReadable = isReadable,
        isLendable = isLendable,
        isPreviewable = isPreviewable,
        identifier = identifier,
        isbn = isbn,
        oclc = oclc,
        openLibraryWork = openLibraryWork,
        openLibraryEdition = openLibraryEdition,
        lastLoanDate = lastLoanDate,
        numWaitlist = numWaitlist,
        lastWaitlistDate = lastWaitlistDate,
        isRestricted = isRestricted,
        isBrowseable = isBrowseable
    )
}
