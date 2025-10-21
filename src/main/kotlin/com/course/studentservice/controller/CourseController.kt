package com.course.studentservice.controller

import com.course.studentservice.dto.*
import com.course.studentservice.service.CourseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Course Management", description = "APIs for managing courses")
class CourseController(
    private val courseService: CourseService
) {
    
    @PostMapping
    @Operation(summary = "Create a new course", description = "Creates a new course with the provided information")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Course created successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Course code already exists")
    ])
    fun createCourse(@Valid @RequestBody request: CreateCourseRequest): ResponseEntity<ApiResponse<CourseDto>> {
        val course = courseService.createCourse(request)
        val response = ApiResponse(
            success = true,
            message = "Course created successfully",
            data = course,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieves a course by its unique identifier")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Course retrieved successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Course not found")
    ])
    fun getCourseById(@PathVariable id: Long): ResponseEntity<ApiResponse<CourseDto>> {
        val course = courseService.getCourseById(id)
        val response = ApiResponse(
            success = true,
            message = "Course retrieved successfully",
            data = course,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/code/{courseCode}")
    @Operation(summary = "Get course by code", description = "Retrieves a course by its course code")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Course retrieved successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Course not found")
    ])
    fun getCourseByCode(@PathVariable courseCode: String): ResponseEntity<ApiResponse<CourseDto>> {
        val course = courseService.getCourseByCode(courseCode)
        val response = ApiResponse(
            success = true,
            message = "Course retrieved successfully",
            data = course,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieves a paginated list of all courses")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    ])
    fun getAllCourses(
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<CourseDto>>> {
        val courses = courseService.getAllCourses(pageable)
        val response = ApiResponse(
            success = true,
            message = "Courses retrieved successfully",
            data = courses,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search courses by title", description = "Searches for courses by title (case-insensitive)")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    ])
    fun searchCoursesByTitle(
        @RequestParam title: String,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<CourseDto>>> {
        val courses = courseService.searchCoursesByTitle(title, pageable)
        val response = ApiResponse(
            success = true,
            message = "Courses retrieved successfully",
            data = courses,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/lecturer/{lecturerId}")
    @Operation(summary = "Get courses by lecturer", description = "Retrieves all courses taught by a specific lecturer")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    ])
    fun getCoursesByLecturer(
        @PathVariable lecturerId: Long,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<CourseDto>>> {
        val courses = courseService.getCoursesByLecturer(lecturerId, pageable)
        val response = ApiResponse(
            success = true,
            message = "Courses retrieved successfully",
            data = courses,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming courses", description = "Retrieves courses that start after today")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    ])
    fun getUpcomingCourses(
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<CourseDto>>> {
        val courses = courseService.getUpcomingCourses(pageable)
        val response = ApiResponse(
            success = true,
            message = "Upcoming courses retrieved successfully",
            data = courses,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get courses by date range", description = "Retrieves courses within a specific date range")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    ])
    fun getCoursesByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<CourseDto>>> {
        val courses = courseService.getCoursesByDateRange(startDate, endDate, pageable)
        val response = ApiResponse(
            success = true,
            message = "Courses retrieved successfully",
            data = courses,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/credits/{credits}")
    @Operation(summary = "Get courses by credits", description = "Retrieves courses with a specific number of credits")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    ])
    fun getCoursesByCredits(
        @PathVariable credits: Int,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<CourseDto>>> {
        val courses = courseService.getCoursesByCredits(credits, pageable)
        val response = ApiResponse(
            success = true,
            message = "Courses retrieved successfully",
            data = courses,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/credits-range")
    @Operation(summary = "Get courses by credits range", description = "Retrieves courses within a specific credits range")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    ])
    fun getCoursesByCreditsRange(
        @RequestParam minCredits: Int,
        @RequestParam maxCredits: Int,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<CourseDto>>> {
        val courses = courseService.getCoursesByCreditsRange(minCredits, maxCredits, pageable)
        val response = ApiResponse(
            success = true,
            message = "Courses retrieved successfully",
            data = courses,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/capacity/{capacity}")
    @Operation(summary = "Get courses by minimum capacity", description = "Retrieves courses with capacity greater than or equal to the specified value")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    ])
    fun getCoursesByMinCapacity(
        @PathVariable capacity: Int,
        @Parameter(description = "Pagination parameters") pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<CourseDto>>> {
        val courses = courseService.getCoursesByMinCapacity(capacity, pageable)
        val response = ApiResponse(
            success = true,
            message = "Courses retrieved successfully",
            data = courses,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update course", description = "Updates an existing course with the provided information")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Course updated successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Course not found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Course code already exists")
    ])
    fun updateCourse(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateCourseRequest
    ): ResponseEntity<ApiResponse<CourseDto>> {
        val course = courseService.updateCourse(id, request)
        val response = ApiResponse(
            success = true,
            message = "Course updated successfully",
            data = course,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course", description = "Deletes a course by its ID")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Course deleted successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Course not found")
    ])
    fun deleteCourse(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        courseService.deleteCourse(id)
        val response = ApiResponse(
            success = true,
            message = "Course deleted successfully",
            data = null,
            timestamp = java.time.LocalDateTime.now()
        )
        return ResponseEntity.ok(response)
    }
}
