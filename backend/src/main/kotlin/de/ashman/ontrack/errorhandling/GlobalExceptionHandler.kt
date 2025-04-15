package de.ashman.ontrack.errorhandling

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.ErrorResponse
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: MethodArgumentNotValidException): ErrorResponse
    {
        val errors = HashMap<String, MutableList<String>>()
        exception
            .bindingResult
            .fieldErrors
            .forEach { error ->
                val fieldErrors = errors.getOrPut(error.field) { mutableListOf() }
                fieldErrors.add(error.defaultMessage.orEmpty())
            }
        val problem = ProblemDetail.forStatus(HttpStatus.CONFLICT)
        problem.type = URI.create(ErrorType.ValidationError.name)
        problem.detail = "Validation failed for one or more fields"
        problem.setProperty("errors", errors)

        return ErrorResponseException(
            HttpStatus.CONFLICT,
            problem,
            exception,
        )
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(exception: ValidationException): ErrorResponse {
        val problem = ProblemDetail.forStatus(HttpStatus.CONFLICT)
        problem.type = URI.create(ErrorType.ValidationError.name)
        problem.detail = "Validation failed"
        problem.setProperty("errors", exception.errors)

        return ErrorResponseException(
            HttpStatus.CONFLICT,
            problem,
            exception,
        )
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(exception: DomainException): ErrorResponse {
        val problem = ProblemDetail.forStatus(HttpStatus.CONFLICT)
        problem.type = URI.create(ErrorType.DomainError.name)
        problem.detail = exception.message

        return ErrorResponseException(
            HttpStatus.CONFLICT,
            problem,
            exception,
        )
    }
}
