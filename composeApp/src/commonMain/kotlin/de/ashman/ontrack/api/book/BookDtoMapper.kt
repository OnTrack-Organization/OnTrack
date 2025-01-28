package de.ashman.ontrack.api.book

import de.ashman.ontrack.api.book.dto.AuthorDto
import de.ashman.ontrack.api.book.dto.BookDto
import de.ashman.ontrack.api.book.dto.BookWorksResponse
import de.ashman.ontrack.api.formatCreatorDate
import de.ashman.ontrack.api.getOpenLibraryCoverUrl
import de.ashman.ontrack.domain.Author
import de.ashman.ontrack.domain.Book

fun BookDto.toDomain(): Book {
    return Book(
        id = key.substringAfter("/works/"),
        title = title,
        coverUrl = coverI.getOpenLibraryCoverUrl(),
        releaseYear = firstPublishYear.toString(),
        language = language,
        numberOfPagesMedian = numberOfPagesMedian,
        publisher = publisher,
        ratingsAverage = ratingsAverage,
        ratingsCount = ratingsCount,
        genres = subject?.filterGenres(),
        author = Author(
            id = authorKey.first().substringAfter("/authors/"),
        )
    )
}

fun String.cleanupDescription(): String {
    return this
        .replace(Regex("""\(\[Source[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\*\*Source\*\*[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""----------[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\(From[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""See https[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""<sup>[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""- Wikipedia[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\[\d+][\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""Source: wikipedia[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .trimEnd()
}

fun List<String>.filterGenres(): List<String>? {
    val relevantGenres = setOf(
        "architecture", "art instruction", "art history", "dance", "design", "fashion", "film",
        "graphic design", "music", "music theory", "painting", "photography", "fantasy", "historical fiction", "horror", "humor",
        "literature", "magic", "mystery and detective stories", "plays", "poetry", "romance",
        "science fiction", "short stories", "thriller", "young adult", "biology", "chemistry",
        "mathematics", "physics", "programming", "management", "entrepreneurship",
        "business economics", "business success", "finance", "kids books", "stories in rhyme",
        "baby books", "bedtime books", "picture books", "ancient civilization", "archaeology",
        "anthropology", "social life and customs", "cooking", "cookbooks",
        "mental health", "exercise", "nutrition", "self-help", "autobiographies", "history",
        "politics and government", "women", "kings and rulers", "composers", "artists",
        "religion", "political science", "psychology", "history textbooks", "mathematics textbooks", "geography",
        "psychology textbooks", "algebra", "education", "business & economics textbooks",
        "science textbooks", "chemistry textbooks", "english language", "physics textbooks",
        "computer science",
    )

    return this.filter { it.lowercase() in relevantGenres }
        .take(5)
        .map { it.replaceFirstChar { char -> char.uppercase() } }
        .takeIf { it.isNotEmpty() }
}

fun BookWorksResponse.toDomain(): Book {
    return Book(
        id = key.substringAfter("/works/"),
        title = title,
        coverUrl = covers?.firstOrNull()?.getOpenLibraryCoverUrl().orEmpty(),
        author = authors?.first()?.author?.toDomain() ?: Author(id = ""),
    )
}

fun AuthorDto.toDomain(): Author {
    return Author(
        id = key.substringAfter("/authors/"),
        name = name,
        imageUrl = photos?.firstOrNull()?.getOpenLibraryCoverUrl(),
        bio = bio?.cleanupDescription(),
        birthDate = birthDate?.formatCreatorDate(),
        deathDate = deathDate?.formatCreatorDate(),
    )
}
