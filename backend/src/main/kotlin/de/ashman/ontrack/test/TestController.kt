package de.ashman.ontrack.test

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/test")
    fun getDummyJson(): Test {
        // Create a dummy response object
        return Test(25)
    }
}

data class Test(
    val test: Int
)
