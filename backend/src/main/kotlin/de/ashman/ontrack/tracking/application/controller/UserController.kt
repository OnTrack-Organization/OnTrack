package de.ashman.ontrack.tracking.application.controller

import de.ashman.ontrack.tracking.domain.User
import de.ashman.ontrack.tracking.infrastructure.UserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val repository: UserRepository
) {
    @PostMapping("/user")
    fun create() {
        val user = User(
            "some Token",
            "John",
            "John Doe1234",
            "somedumm@example.com",
            ""
        )

        this.repository.saveAndFlush(user)
    }

    @GetMapping("/users")
    fun getUsers(): List<User> {
        return this.repository.findAll()
    }

    @GetMapping("/test")
    fun test(): String
    {
        return "Hi you finally reached me"
    }
}
