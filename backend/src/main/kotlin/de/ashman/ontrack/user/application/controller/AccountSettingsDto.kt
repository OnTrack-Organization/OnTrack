package de.ashman.ontrack.user.application.controller

import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class AccountSettingsDto(
    @field:Length(min = 6, max = 25)
    @field:Pattern(
        regexp = "[a-z0-9_.]*",
        message = "Only lowercase letters, numbers and special characters ._ are allowed"
    )
    val username: String,
    val name: String
)
