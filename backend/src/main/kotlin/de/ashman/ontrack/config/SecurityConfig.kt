package de.ashman.ontrack.config

import jakarta.servlet.http.HttpServletResponse
import org.apache.http.HttpHeaders
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val firebaseAuthFilter: FirebaseAuthFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint { _, response, _ ->
                    // Return 401 and WWW-Authenticate instead of 403
                    response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                }
            }
            .addFilterBefore(firebaseAuthFilter, AuthorizationFilter::class.java)

        return http.build()
    }

    @Bean
    fun firebaseAuthFilterRegistration(filter: FirebaseAuthFilter): FilterRegistrationBean<FirebaseAuthFilter> {
        val registration: FilterRegistrationBean<FirebaseAuthFilter> = FilterRegistrationBean<FirebaseAuthFilter>(filter)

        registration.isEnabled = false

        return registration
    }
}
