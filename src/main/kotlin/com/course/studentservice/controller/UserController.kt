package com.course.studentservice.controller

import com.course.studentservice.dto.*
import com.course.studentservice.entity.UserRole
import com.course.studentservice.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
class UserController(
    private val userService: UserService
) {
    
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists")
    ])
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(user, "User created successfully"))
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    ])
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"))
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    ])
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.getUserByEmail(email)
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"))
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    ])
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"))
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    ])
    fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        userService.deleteUser(id)
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"))
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    ])
    fun getAllUsers(pageable: Pageable): ResponseEntity<ApiResponse<Page<UserDto>>> {
        val users = userService.getAllUsers(pageable)
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"))
    }
    
    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieves users filtered by their role")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    ])
    fun getUsersByRole(
        @PathVariable role: UserRole,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<UserDto>>> {
        val users = userService.getUsersByRole(role, pageable)
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"))
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search users by name", description = "Searches users by first name or last name")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    ])
    fun searchUsers(
        @Parameter(description = "Name to search for") @RequestParam name: String,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<UserDto>>> {
        val users = userService.searchUsers(name, pageable)
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"))
    }
}