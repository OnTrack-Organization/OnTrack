package de.ashman.ontrack.config

import de.ashman.ontrack.security.FirebaseAuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class FirebaseAuthFilter(
    private val firebaseAuthService: FirebaseAuthService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")

        if (token.isNullOrEmpty()) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val firebaseToken = firebaseAuthService.verifyIdToken(token)

            val identity = Identity(
                id = firebaseToken.uid,
                email = firebaseToken.email,
                name = firebaseToken.name,
                picture = firebaseToken.picture
            )

            val authentication = UsernamePasswordAuthenticationToken(identity, null, listOf(SimpleGrantedAuthority("ROLE_USER")))
            SecurityContextHolder.getContext().authentication = authentication

        } catch (e: Exception) {
            e.printStackTrace()
        }

        filterChain.doFilter(request, response)
    }
}
