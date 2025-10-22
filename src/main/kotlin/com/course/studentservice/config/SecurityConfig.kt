package com.course.studentservice.config

import com.course.studentservice.security.CustomUserDetailsService
import com.course.studentservice.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    
    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(customUserDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }
    
    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
    
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { authz ->
                authz
                    // Public endpoints
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    .requestMatchers("/api/v1/auth/register").permitAll()
                    .requestMatchers("/api/v1/auth/refresh").permitAll()
                    .requestMatchers("/api/v1/health/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/swagger-ui.html").permitAll()
                    .requestMatchers("/webjars/**").permitAll()
                    .requestMatchers("/swagger-resources/**").permitAll()
                    
                    // Admin only endpoints
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    
                    // All other endpoints - allow all authenticated users (method-level security will handle roles)
                    .requestMatchers("/api/v1/users", "/api/v1/users/**").authenticated()
                    .requestMatchers("/api/v1/courses", "/api/v1/courses/**").authenticated()
                    .requestMatchers("/api/v1/lecturers", "/api/v1/lecturers/**").authenticated()
                    .requestMatchers("/api/v1/enrollments", "/api/v1/enrollments/**").authenticated()
                    .requestMatchers("/api/v1/students", "/api/v1/students/**").authenticated()
                    
                    // Auth endpoints (authenticated users)
                    .requestMatchers("/api/v1/auth/**").authenticated()
                    
                    .anyRequest().authenticated()
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        
        return http.build()
    }
}