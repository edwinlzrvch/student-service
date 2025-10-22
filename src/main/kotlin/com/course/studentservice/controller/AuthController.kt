package com.course.studentservice.controller

import com.course.studentservice.dto.*
import com.course.studentservice.service.AuthenticationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
class AuthController(
    private val authenticationService: AuthenticationService
) {
    
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Registers a new student user and returns JWT token")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Registration successful"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or email already exists"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists")
    ])
    fun register(@Valid @RequestBody request: RegistrationRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResponse = authenticationService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authResponse, "Registration successful"))
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns JWT token")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    ])
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResponse = authenticationService.login(request)
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"))
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refreshes the JWT token using refresh token")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid refresh token"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    ])
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResponse = authenticationService.refreshToken(request)
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Token refreshed successfully"))
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logs out the current user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout successful"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun logout(): ResponseEntity<ApiResponse<String>> {
        val message = authenticationService.logout()
        return ResponseEntity.ok(ApiResponse.success(message))
    }
    
    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Changes the current user's password")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized or invalid current password"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    ])
    fun changePassword(@Valid @RequestBody request: ChangePasswordRequest): ResponseEntity<ApiResponse<String>> {
        val message = authenticationService.changePassword(request)
        return ResponseEntity.ok(ApiResponse.success(message))
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the current authenticated user's information")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun getCurrentUser(): ResponseEntity<ApiResponse<UserDto>> {
        val user = authenticationService.getCurrentUser()
        val userDto = UserDto(
            userId = user.userId,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            role = user.role,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
        return ResponseEntity.ok(ApiResponse.success(userDto, "User information retrieved successfully"))
    }
    
    @GetMapping("/check-role/{role}")
    @Operation(summary = "Check user role", description = "Checks if the current user has a specific role")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = [
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role check completed"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    ])
    fun checkRole(@PathVariable role: String): ResponseEntity<ApiResponse<Boolean>> {
        val hasRole = when (role.uppercase()) {
            "ADMIN" -> authenticationService.isAdmin()
            "LECTURER" -> authenticationService.isLecturer()
            "STUDENT" -> authenticationService.isStudent()
            else -> false
        }
        return ResponseEntity.ok(ApiResponse.success(hasRole, "Role check completed"))
    }
    
}
