package com.course.studentservice.controller

import com.course.studentservice.dto.*
import com.course.studentservice.entity.EnrollmentStatus
import com.course.studentservice.service.EnrollmentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/enrollments")
@Tag(name = "Enrollment Management", description = "APIs for managing course enrollments")
@SecurityRequirement(name = "bearerAuth")
class EnrollmentController(
    private val enrollmentService: EnrollmentService
) {
    
    @GetMapping
    @Operation(summary = "Get all enrollments", description = "Retrieves a paginated list of all enrollments")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully")
    ])
    fun getAllEnrollments(pageable: Pageable): ResponseEntity<ApiResponse<Page<EnrollmentDto>>> {
        val enrollments = enrollmentService.getAllEnrollments(pageable)
        return ResponseEntity.ok(ApiResponse.success(enrollments, "Enrollments retrieved successfully"))
    }
    
    @PostMapping
    @Operation(summary = "Enroll student in course", description = "Enrolls a student in a specific course")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Student enrolled successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or enrollment not possible"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student or course not found")
    ])
    fun enrollStudent(@Valid @RequestBody request: CreateEnrollmentRequest): ResponseEntity<ApiResponse<EnrollmentDto>> {
        val enrollment = enrollmentService.enrollStudent(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(enrollment, "Student enrolled successfully"))
    }
    
    @PutMapping("/{enrollmentId}/drop")
    @Operation(summary = "Drop course", description = "Allows a student to drop a course")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Course dropped successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Enrollment not found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot drop course")
    ])
    fun dropCourse(@PathVariable enrollmentId: Long): ResponseEntity<ApiResponse<EnrollmentDto>> {
        val enrollment = enrollmentService.dropCourse(enrollmentId)
        return ResponseEntity.ok(ApiResponse.success(enrollment, "Course dropped successfully"))
    }
    
    @PutMapping("/{enrollmentId}")
    @Operation(summary = "Update enrollment", description = "Updates enrollment status or grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Enrollment updated successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Enrollment not found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    ])
    fun updateEnrollment(
        @PathVariable enrollmentId: Long,
        @Valid @RequestBody request: UpdateEnrollmentRequest
    ): ResponseEntity<ApiResponse<EnrollmentDto>> {
        val enrollment = enrollmentService.updateEnrollment(enrollmentId, request)
        return ResponseEntity.ok(ApiResponse.success(enrollment, "Enrollment updated successfully"))
    }
    
    @GetMapping("/{enrollmentId}")
    @Operation(summary = "Get enrollment by ID", description = "Retrieves a specific enrollment by its ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Enrollment found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Enrollment not found")
    ])
    fun getEnrollmentById(@PathVariable enrollmentId: Long): ResponseEntity<ApiResponse<EnrollmentDto>> {
        val enrollment = enrollmentService.getEnrollmentById(enrollmentId)
        return ResponseEntity.ok(ApiResponse.success(enrollment, "Enrollment retrieved successfully"))
    }
    
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student enrollments", description = "Retrieves all enrollments for a specific student")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    ])
    fun getStudentEnrollments(
        @PathVariable studentId: Long,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<EnrollmentDto>>> {
        val enrollments = enrollmentService.getStudentEnrollments(studentId, pageable)
        return ResponseEntity.ok(ApiResponse.success(enrollments, "Student enrollments retrieved successfully"))
    }
    
    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get course enrollments", description = "Retrieves all enrollments for a specific course")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Course not found")
    ])
    fun getCourseEnrollments(
        @PathVariable courseId: Long,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<EnrollmentDto>>> {
        val enrollments = enrollmentService.getCourseEnrollments(courseId, pageable)
        return ResponseEntity.ok(ApiResponse.success(enrollments, "Course enrollments retrieved successfully"))
    }
    
    @GetMapping("/lecturer/{lecturerId}")
    @Operation(summary = "Get lecturer course enrollments", description = "Retrieves all enrollments for courses taught by a specific lecturer")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lecturer not found")
    ])
    fun getLecturerCourseEnrollments(
        @PathVariable lecturerId: Long,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<EnrollmentDto>>> {
        val enrollments = enrollmentService.getLecturerCourseEnrollments(lecturerId, pageable)
        return ResponseEntity.ok(ApiResponse.success(enrollments, "Lecturer course enrollments retrieved successfully"))
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get enrollments by status", description = "Retrieves all enrollments with a specific status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully")
    ])
    fun getEnrollmentsByStatus(
        @PathVariable status: EnrollmentStatus,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<EnrollmentDto>>> {
        val enrollments = enrollmentService.getEnrollmentsByStatus(status, pageable)
        return ResponseEntity.ok(ApiResponse.success(enrollments, "Enrollments retrieved successfully"))
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get enrollment statistics", description = "Retrieves enrollment statistics for dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    ])
    fun getEnrollmentStats(): ResponseEntity<ApiResponse<EnrollmentStatsDto>> {
        val stats = enrollmentService.getEnrollmentStats()
        return ResponseEntity.ok(ApiResponse.success(stats, "Enrollment statistics retrieved successfully"))
    }
}
