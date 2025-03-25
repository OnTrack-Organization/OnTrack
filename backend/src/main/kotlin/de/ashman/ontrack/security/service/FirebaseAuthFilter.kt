package de.ashman.ontrack.security.service

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.stereotype.Component

@Component
class FirebaseAuthFilter(
    private val firebaseAuthService: FirebaseAuthService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        // Skip authentication for '/auth' endpoint
        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")

        if (token.isNullOrEmpty()) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }

        try {
            firebaseAuthService.verifyIdToken(token)
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
        }
    }
}
