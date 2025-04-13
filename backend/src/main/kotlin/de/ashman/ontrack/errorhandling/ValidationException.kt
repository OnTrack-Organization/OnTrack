package de.ashman.ontrack.errorhandling

class ValidationException(
    val errors: MutableMap<String, List<String>>,
) : RuntimeException() {
    constructor(field: String, message: String?) : this(mutableMapOf()) {
        errors[field] = listOf(message.orEmpty())
    }
}
