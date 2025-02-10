package de.ashman.ontrack.api.utils

fun Int?.getOpenLibraryCoverUrl(): String = this?.let { "https://covers.openlibrary.org/b/id/${it}-L.jpg" }.orEmpty()

fun String.cleanupBookKey(): String = this.substringAfter("/works/")
fun String.cleanupAuthorKey(): String = this.substringAfter("/authors/")

fun String.cleanupDescription() =
    this.replace(Regex("""\(\[Source[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\*\*Source\*\*[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""----------[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\(From[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""See https[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""<sup>[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""- Wikipedia[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\[\d+][\s\S]*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""Source: wikipedia[\s\S]*""", RegexOption.IGNORE_CASE), "")
        .trimEnd()

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

fun String.extractYear(): String? {
    val regex = """\b\d{4}\b""".toRegex()
    return regex.find(this)?.value
}