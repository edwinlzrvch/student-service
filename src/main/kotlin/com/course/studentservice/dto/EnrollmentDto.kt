package com.course.studentservice.dto

import com.course.studentservice.entity.EnrollmentStatus
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

data class EnrollmentDto(
    val enrollmentId: Long? = null,
    val student: StudentDto? = null,
    val course: CourseDto? = null,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val enrollmentDate: LocalDateTime? = null,
    val status: EnrollmentStatus? = null,
    val grade: BigDecimal? = null,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastUpdated: LocalDateTime? = null
)

data class CreateEnrollmentRequest(
    @field:NotNull(message = "Student ID is required")
    val studentId: Long,
    
    @field:NotNull(message = "Course ID is required")
    val courseId: Long
)

data class UpdateEnrollmentRequest(
    val status: EnrollmentStatus? = null,
    @field:DecimalMin(value = "0.0", message = "Grade must be at least 0.0")
    @field:DecimalMax(value = "10.0", message = "Grade must be at most 10.0")
    val grade: BigDecimal? = null
)

data class EnrollmentStatsDto(
    val totalEnrollments: Long,
    val activeEnrollments: Long,
    val completedEnrollments: Long,
    val droppedEnrollments: Long
)

data class LecturerStatsDto(
    val lecturerId: Long,
    val lecturerName: String,
    val totalCourses: Long,
    val totalStudents: Long,
    val activeStudents: Long,
    val completedStudents: Long
)

data class DashboardOverviewDto(
    val enrollmentStats: EnrollmentStatsDto,
    val totalStudents: Long,
    val totalCourses: Long,
    val totalUsers: Long
)

data class CourseEnrollmentStatsDto(
    val courseId: Long,
    val courseCode: String,
    val title: String,
    val enrollmentCount: Long,
    val capacity: Int?,
    val enrollmentRate: Double
)

data class EnrollmentTrendsDto(
    val dailyEnrollments: Map<String, Long>,
    val totalEnrollments: Long,
    val averageDailyEnrollments: Double
)
