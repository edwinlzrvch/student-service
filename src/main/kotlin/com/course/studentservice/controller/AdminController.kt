package com.course.studentservice.controller

import com.course.studentservice.dto.*
import com.course.studentservice.service.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Dashboard", description = "APIs for administrators to monitor the system")
class AdminController(
    private val enrollmentService: EnrollmentService,
    private val studentService: StudentService,
    private val courseService: CourseService,
    private val userService: UserService
) {
    
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard overview", description = "Retrieves comprehensive dashboard data for administrators")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully")
    ])
    fun getDashboardOverview(): ResponseEntity<ApiResponse<DashboardOverviewDto>> {
        val enrollmentStats = enrollmentService.getEnrollmentStats()
        val totalStudents = studentService.getTotalStudentCount()
        val totalCourses = courseService.getTotalCourseCount()
        val totalUsers = userService.getTotalUserCount()
        
        val dashboard = DashboardOverviewDto(
            enrollmentStats = enrollmentStats,
            totalStudents = totalStudents,
            totalCourses = totalCourses,
            totalUsers = totalUsers
        )
        
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Dashboard data retrieved successfully"))
    }
    
    @GetMapping("/enrollments")
    @Operation(summary = "Get all enrollments", description = "Retrieves all enrollments with pagination for admin monitoring")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully")
    ])
    fun getAllEnrollments(
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<EnrollmentDto>>> {
        val enrollments = enrollmentService.getAllEnrollments(pageable)
        return ResponseEntity.ok(ApiResponse.success(enrollments, "All enrollments retrieved successfully"))
    }
    
    @GetMapping("/enrollments/recent")
    @Operation(summary = "Get recent enrollments", description = "Retrieves recent enrollments for monitoring")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recent enrollments retrieved successfully")
    ])
    fun getRecentEnrollments(
        @Parameter(description = "Number of days to look back") @RequestParam(defaultValue = "7") days: Int,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<EnrollmentDto>>> {
        val enrollments = enrollmentService.getRecentEnrollments(days, pageable)
        return ResponseEntity.ok(ApiResponse.success(enrollments, "Recent enrollments retrieved successfully"))
    }
    
    @GetMapping("/students/active")
    @Operation(summary = "Get active students", description = "Retrieves all active students for monitoring")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active students retrieved successfully")
    ])
    fun getActiveStudents(
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<StudentDto>>> {
        val students = studentService.getActiveStudents(pageable)
        return ResponseEntity.ok(ApiResponse.success(students, "Active students retrieved successfully"))
    }
    
    @GetMapping("/courses/popular")
    @Operation(summary = "Get popular courses", description = "Retrieves courses with highest enrollment counts")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Popular courses retrieved successfully")
    ])
    fun getPopularCourses(
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<CourseEnrollmentStatsDto>>> {
        val courses = courseService.getPopularCourses(pageable)
        return ResponseEntity.ok(ApiResponse.success(courses, "Popular courses retrieved successfully"))
    }
    
    @GetMapping("/reports/enrollment-trends")
    @Operation(summary = "Get enrollment trends", description = "Retrieves enrollment trends over time")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Enrollment trends retrieved successfully")
    ])
    fun getEnrollmentTrends(
        @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<ApiResponse<EnrollmentTrendsDto>> {
        val trends = enrollmentService.getEnrollmentTrends(startDate, endDate)
        return ResponseEntity.ok(ApiResponse.success(trends, "Enrollment trends retrieved successfully"))
    }
}
