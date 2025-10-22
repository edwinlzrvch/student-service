package com.course.studentservice.service

import com.course.studentservice.dto.*
import com.course.studentservice.entity.User
import com.course.studentservice.entity.UserRole
import com.course.studentservice.repository.UserRepository
import com.course.studentservice.security.UserPrincipal
import com.course.studentservice.util.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class AuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager,
    private val studentService: StudentService
) {
    
    fun register(request: RegistrationRequest): AuthResponse {
        // Check if user already exists
        if (userRepository.findByEmail(request.email).isPresent) {
            throw IllegalArgumentException("Email already exists")
        }
        
        // Create new user with Student role
        val hashedPassword = passwordEncoder.encode(request.password)
        val user = User(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            passwordHash = hashedPassword,
            role = UserRole.Student,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val savedUser = userRepository.save(user)
        
        // Create student record
        studentService.createStudentFromUser(savedUser, request.dateOfBirth)
        
        // Generate token for immediate login
        val userPrincipal = UserPrincipal(savedUser)
        val token = jwtUtil.generateToken(userPrincipal, savedUser.userId, savedUser.role!!)
        
        return AuthResponse(
            token = token,
            userId = savedUser.userId,
            email = savedUser.email,
            firstName = savedUser.firstName,
            lastName = savedUser.lastName,
            role = savedUser.role!!,
            expiresIn = 86400000 // 24 hours in milliseconds
        )
    }
    
    fun login(request: LoginRequest): AuthResponse {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
            
            val userPrincipal = authentication.principal as UserPrincipal
            val user = userPrincipal.getCurrentUser()
            
            val token = jwtUtil.generateToken(userPrincipal, user.userId, user.role!!)
            val refreshToken = jwtUtil.generateRefreshToken(userPrincipal, user.userId, user.role!!)
            
            return AuthResponse(
                token = token,
                userId = user.userId,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                role = user.role!!,
                expiresIn = 86400000 // 24 hours in milliseconds
            )
        } catch (e: Exception) {
            throw BadCredentialsException("Invalid email or password")
        }
    }
    
    fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        val token = request.refreshToken
        
        if (!jwtUtil.validateToken(token) || !jwtUtil.isRefreshToken(token)) {
            throw BadCredentialsException("Invalid refresh token")
        }
        
        val username = jwtUtil.extractUsername(token)
            ?: throw BadCredentialsException("Invalid refresh token")
        
        val user = userRepository.findByEmail(username)
            .orElseThrow { BadCredentialsException("User not found") }
        
        val userPrincipal = UserPrincipal(user)
        val newToken = jwtUtil.generateToken(userPrincipal, user.userId, user.role!!)
        val newRefreshToken = jwtUtil.generateRefreshToken(userPrincipal, user.userId, user.role!!)
        
        return AuthResponse(
            token = newToken,
            userId = user.userId,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            role = user.role!!,
            expiresIn = 86400000
        )
    }
    
    fun changePassword(request: ChangePasswordRequest): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val userPrincipal = authentication.principal as UserPrincipal
        val user = userPrincipal.user
        
        if (!passwordEncoder.matches(request.currentPassword, user.passwordHash)) {
            throw BadCredentialsException("Current password is incorrect")
        }
        
        val newPasswordHash = passwordEncoder.encode(request.newPassword)
        val updatedUser = user.copy(
            passwordHash = newPasswordHash,
            updatedAt = LocalDateTime.now()
        )
        
        userRepository.save(updatedUser)
        return "Password changed successfully"
    }
    
    fun logout(): String {
        SecurityContextHolder.clearContext()
        return "Logged out successfully"
    }
    
    fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        val userPrincipal = authentication.principal as UserPrincipal
        return userPrincipal.getCurrentUser()
    }
    
    fun hasRole(role: UserRole): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.authorities.any { it.authority == "ROLE_${role.name}" }
    }
    
    fun isAdmin(): Boolean = hasRole(UserRole.Admin)
    fun isLecturer(): Boolean = hasRole(UserRole.Lecturer)
    fun isStudent(): Boolean = hasRole(UserRole.Student)
}
