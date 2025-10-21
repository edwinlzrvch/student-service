package com.course.studentservice.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.*
import java.time.LocalDate
import java.time.LocalDateTime

data class CourseDto(
    val courseId: Long? = null,
    val courseCode: String? = null,
    val title: String? = null,
    val description: String? = null,
    val credits: Int? = null,
    val lecturer: LecturerDto? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate? = null,
    val capacity: Int? = null,
    val courseMetadata: String? = null,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime? = null
)

data class CreateCourseRequest(
    @field:NotBlank(message = "Course code is required")
    @field:Size(max = 20, message = "Course code must not exceed 20 characters")
    val courseCode: String,
    
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 100, message = "Title must not exceed 100 characters")
    val title: String,
    
    val description: String? = null,
    
    @field:Min(value = 1, message = "Credits must be at least 1")
    @field:Max(value = 10, message = "Credits must not exceed 10")
    val credits: Int? = null,
    
    val lecturerId: Long? = null,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate? = null,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate? = null,
    
    @field:Min(value = 1, message = "Capacity must be at least 1")
    val capacity: Int? = null,
    
    val courseMetadata: String? = null
)

data class UpdateCourseRequest(
    @field:Size(max = 20, message = "Course code must not exceed 20 characters")
    val courseCode: String? = null,
    
    @field:Size(max = 100, message = "Title must not exceed 100 characters")
    val title: String? = null,
    
    val description: String? = null,
    
    @field:Min(value = 1, message = "Credits must be at least 1")
    @field:Max(value = 10, message = "Credits must not exceed 10")
    val credits: Int? = null,
    
    val lecturerId: Long? = null,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate? = null,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate? = null,
    
    @field:Min(value = 1, message = "Capacity must be at least 1")
    val capacity: Int? = null,
    
    val courseMetadata: String? = null
)
