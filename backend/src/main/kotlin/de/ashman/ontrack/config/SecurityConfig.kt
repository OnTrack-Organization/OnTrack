package de.ashman.ontrack.config

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
            .addFilterBefore(firebaseAuthFilter, AuthorizationFilter::class.java)

        return http.build()
    }

    /**
     * Disables [FirebaseAuthFilter] in embedded container as per
     * [spring security doc](https://docs.spring.io/spring-security/reference/servlet/architecture.html#_declaring_your_filter_as_a_bean).
     * to avoid duplicate execution
     */
    @Bean
    fun firebaseAuthFilterRegistration(filter: FirebaseAuthFilter): FilterRegistrationBean<FirebaseAuthFilter> {
        val registration: FilterRegistrationBean<FirebaseAuthFilter> =
            FilterRegistrationBean<FirebaseAuthFilter>(filter)
        registration.isEnabled = false

        return registration
    }
}
