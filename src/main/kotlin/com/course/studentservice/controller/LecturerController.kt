package com.course.studentservice.controller

import com.course.studentservice.dto.*
import com.course.studentservice.service.CourseService
import com.course.studentservice.service.EnrollmentService
import com.course.studentservice.service.LecturerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/lecturers")
@Tag(name = "Lecturer Management", description = "APIs for lecturers to manage their courses and view students")
@SecurityRequirement(name = "bearerAuth")
class LecturerController(
    private val lecturerService: LecturerService,
    private val courseService: CourseService,
    private val enrollmentService: EnrollmentService
) {
    
    @GetMapping
    @Operation(summary = "Get all lecturers", description = "Retrieves a paginated list of all lecturers")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lecturers retrieved successfully")
    ])
    fun getAllLecturers(pageable: Pageable): ResponseEntity<ApiResponse<Page<LecturerDto>>> {
        val lecturers = lecturerService.getAllLecturers(pageable)
        return ResponseEntity.ok(ApiResponse.success(lecturers, "Lecturers retrieved successfully"))
    }
    
    @PostMapping
    @Operation(summary = "Create a new lecturer", description = "Creates a new lecturer with the provided information")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Lecturer created successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists")
    ])
    fun createLecturer(@Valid @RequestBody request: CreateLecturerRequest): ResponseEntity<ApiResponse<LecturerDto>> {
        val lecturer = lecturerService.createLecturer(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(lecturer, "Lecturer created successfully"))
    }
    
    @GetMapping("/{lecturerId}/courses")
    @Operation(summary = "Get lecturer courses", description = "Retrieves all courses taught by a specific lecturer")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lecturer not found")
    ])
    fun getLecturerCourses(
        @PathVariable lecturerId: Long,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<CourseDto>>> {
        val courses = courseService.getCoursesByLecturer(lecturerId, pageable)
        return ResponseEntity.ok(ApiResponse.success(courses, "Lecturer courses retrieved successfully"))
    }
    
    @GetMapping("/{lecturerId}/students")
    @Operation(summary = "Get lecturer's students", description = "Retrieves all students enrolled in courses taught by a specific lecturer")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lecturer not found")
    ])
    fun getLecturerStudents(
        @PathVariable lecturerId: Long,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<EnrollmentDto>>> {
        val enrollments = enrollmentService.getLecturerCourseEnrollments(lecturerId, pageable)
        return ResponseEntity.ok(ApiResponse.success(enrollments, "Lecturer students retrieved successfully"))
    }
    
    @GetMapping("/{lecturerId}/courses/{courseId}/students")
    @Operation(summary = "Get students for specific course", description = "Retrieves all students enrolled in a specific course taught by the lecturer")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lecturer or course not found")
    ])
    fun getCourseStudents(
        @PathVariable lecturerId: Long,
        @PathVariable courseId: Long,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<EnrollmentDto>>> {
        val enrollments = enrollmentService.getCourseEnrollments(courseId, pageable)
        return ResponseEntity.ok(ApiResponse.success(enrollments, "Course students retrieved successfully"))
    }
    
    @GetMapping("/{lecturerId}")
    @Operation(summary = "Get lecturer by ID", description = "Retrieves lecturer information by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lecturer found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lecturer not found")
    ])
    fun getLecturerById(@PathVariable lecturerId: Long): ResponseEntity<ApiResponse<LecturerDto>> {
        val lecturer = lecturerService.getLecturerById(lecturerId)
        return ResponseEntity.ok(ApiResponse.success(lecturer, "Lecturer retrieved successfully"))
    }
    
    @GetMapping("/{lecturerId}/stats")
    @Operation(summary = "Get lecturer statistics", description = "Retrieves statistics for a lecturer's courses and students")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lecturer not found")
    ])
    fun getLecturerStats(@PathVariable lecturerId: Long): ResponseEntity<ApiResponse<LecturerStatsDto>> {
        val stats = lecturerService.getLecturerStats(lecturerId)
        return ResponseEntity.ok(ApiResponse.success(stats, "Lecturer statistics retrieved successfully"))
    }
}
