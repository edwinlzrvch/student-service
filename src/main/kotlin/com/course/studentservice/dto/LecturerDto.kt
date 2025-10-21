package com.course.studentservice.dto

import com.course.studentservice.entity.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class LecturerDto(
    val lecturerId: Long,
    val user: UserDto,
    val specialization: String?,
    val hireDate: LocalDate?,
    val phoneNumber: String?
)

data class CreateLecturerRequest(
    @field:NotBlank(message = "First name is required")
    val firstName: String,
    
    @field:NotBlank(message = "Last name is required")
    val lastName: String,
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String,
    
    val specialization: String? = null,
    val phoneNumber: String? = null,
    val hireDate: LocalDate? = null
)

data class UpdateLecturerRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val specialization: String? = null,
    val phoneNumber: String? = null
)
