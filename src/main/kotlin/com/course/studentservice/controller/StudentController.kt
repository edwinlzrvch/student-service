package com.course.studentservice.controller

import com.course.studentservice.dto.*
import com.course.studentservice.service.StudentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Student Management", description = "APIs for managing students")
@SecurityRequirement(name = "bearerAuth")
class StudentController(
    private val studentService: StudentService
) {
    
    @PostMapping
    @Operation(summary = "Create a new student", description = "Creates a new student with the provided information")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Student created successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists")
    ])
    fun createStudent(@Valid @RequestBody request: CreateStudentRequest): ResponseEntity<ApiResponse<StudentDto>> {
        val student = studentService.createStudent(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(student, "Student created successfully"))
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieves a student by their unique identifier")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    ])
    fun getStudentById(@PathVariable id: Long): ResponseEntity<ApiResponse<StudentDto>> {
        val student = studentService.getStudentById(id)
        return ResponseEntity.ok(ApiResponse.success(student, "Student retrieved successfully"))
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get student by email", description = "Retrieves a student by their email address")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    ])
    fun getStudentByEmail(@PathVariable email: String): ResponseEntity<ApiResponse<StudentDto>> {
        val student = studentService.getStudentByEmail(email)
        return ResponseEntity.ok(ApiResponse.success(student, "Student retrieved successfully"))
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update student", description = "Updates an existing student's information")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER', 'STUDENT')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student updated successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    ])
    fun updateStudent(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateStudentRequest
    ): ResponseEntity<ApiResponse<StudentDto>> {
        val student = studentService.updateStudent(id, request)
        return ResponseEntity.ok(ApiResponse.success(student, "Student updated successfully"))
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student", description = "Deletes a student by their ID")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student deleted successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    ])
    fun deleteStudent(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        studentService.deleteStudent(id)
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully"))
    }
    
    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieves a paginated list of all students")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Students retrieved successfully")
    ])
    fun getAllStudents(pageable: Pageable): ResponseEntity<ApiResponse<Page<StudentDto>>> {
        val students = studentService.getAllStudents(pageable)
        return ResponseEntity.ok(ApiResponse.success(students, "Students retrieved successfully"))
    }
    
    @GetMapping("/enrollment-date-range")
    @Operation(summary = "Get students by enrollment date range", description = "Retrieves students enrolled within a specific date range")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Students retrieved successfully")
    ])
    fun getStudentsByEnrollmentDateRange(
        @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<StudentDto>>> {
        val students = studentService.getStudentsByEnrollmentDateRange(startDate, endDate, pageable)
        return ResponseEntity.ok(ApiResponse.success(students, "Students retrieved successfully"))
    }
}