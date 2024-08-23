package de.ashman.ontrack

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform