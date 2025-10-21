package com.course.studentservice.dto

import com.course.studentservice.entity.UserRole
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class UserDto(
    val userId: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,
    val passwordHash: String? = null,
    val role: UserRole? = null,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime? = null,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime? = null
)

data class CreateUserRequest(
    @field:NotBlank(message = "First name is required")
    @field:Size(max = 50, message = "First name must not exceed 50 characters")
    val firstName: String,
    
    @field:NotBlank(message = "Last name is required")
    @field:Size(max = 50, message = "Last name must not exceed 50 characters")
    val lastName: String,
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    @field:Size(max = 100, message = "Email must not exceed 100 characters")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,
    
    val role: UserRole
)

data class UpdateUserRequest(
    @field:Size(max = 50, message = "First name must not exceed 50 characters")
    val firstName: String? = null,
    
    @field:Size(max = 50, message = "Last name must not exceed 50 characters")
    val lastName: String? = null,
    
    @field:Email(message = "Invalid email format")
    @field:Size(max = 100, message = "Email must not exceed 100 characters")
    val email: String? = null,
    
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String? = null
)
