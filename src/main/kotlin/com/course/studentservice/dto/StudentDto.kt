package com.course.studentservice.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class StudentDto(
    val studentId: Long? = null,
    val user: UserDto? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val dateOfBirth: LocalDate? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val enrollmentDate: LocalDate? = null
)

data class CreateStudentRequest(
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
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val dateOfBirth: LocalDate? = null,
    
    @field:Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String? = null,
    
    @field:Size(max = 255, message = "Address must not exceed 255 characters")
    val address: String? = null,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val enrollmentDate: LocalDate? = null
)

data class UpdateStudentRequest(
    @field:Size(max = 50, message = "First name must not exceed 50 characters")
    val firstName: String? = null,
    
    @field:Size(max = 50, message = "Last name must not exceed 50 characters")
    val lastName: String? = null,
    
    @field:Email(message = "Invalid email format")
    @field:Size(max = 100, message = "Email must not exceed 100 characters")
    val email: String? = null,
    
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String? = null,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val dateOfBirth: LocalDate? = null,
    
    @field:Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String? = null,
    
    @field:Size(max = 255, message = "Address must not exceed 255 characters")
    val address: String? = null,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val enrollmentDate: LocalDate? = null
)
