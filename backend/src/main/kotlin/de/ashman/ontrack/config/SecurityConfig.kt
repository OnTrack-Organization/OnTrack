package de.ashman.ontrack.config

import de.ashman.ontrack.security.service.FirebaseAuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
// @EnableWebSecurity(debug = true) // enable for debug
class SecurityConfig(
    private val firebaseAuthFilter: FirebaseAuthFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { csrf -> csrf.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/auth").permitAll() // Allow unauthenticated access to /auth
                    .anyRequest().authenticated() // Require authentication for other requests
            }
            .addFilterBefore(firebaseAuthFilter, BasicAuthenticationFilter::class.java) // Add the filter for other routes

        return http.build()
    }
}
