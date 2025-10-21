package com.course.studentservice.service

import com.course.studentservice.dto.*
import com.course.studentservice.entity.User
import com.course.studentservice.entity.UserRole
import com.course.studentservice.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    
    fun createUser(request: CreateUserRequest): UserDto {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("User with email ${request.email} already exists")
        }
        
        val user = User(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = request.role
        )
        
        val savedUser = userRepository.save(user)
        return mapToDto(savedUser)
    }
    
    fun getUserById(id: Long): UserDto {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User with id $id not found") }
        return mapToDto(user)
    }
    
    fun getUserByEmail(email: String): UserDto {
        val user = userRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("User with email $email not found") }
        return mapToDto(user)
    }
    
    fun updateUser(id: Long, request: UpdateUserRequest): UserDto {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User with id $id not found") }
        
        var updatedUser = user
        request.firstName?.let { updatedUser = updatedUser.copy(firstName = it) }
        request.lastName?.let { updatedUser = updatedUser.copy(lastName = it) }
        request.email?.let { 
            if (userRepository.existsByEmail(it) && it != user.email) {
                throw IllegalArgumentException("Email $it is already in use")
            }
            updatedUser = updatedUser.copy(email = it)
        }
        request.password?.let { updatedUser = updatedUser.copy(passwordHash = passwordEncoder.encode(it)) }
        
        val savedUser = userRepository.save(updatedUser)
        return mapToDto(savedUser)
    }
    
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("User with id $id not found")
        }
        userRepository.deleteById(id)
    }
    
    fun getAllUsers(pageable: Pageable): Page<UserDto> {
        return userRepository.findAll(pageable).map { mapToDto(it) }
    }
    
    fun getUsersByRole(role: UserRole, pageable: Pageable): Page<UserDto> {
        val users = userRepository.findByRole(role)
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, users.size)
        val pageUsers = users.subList(start, end)
        val page = PageImpl(pageUsers, pageable, users.size.toLong())
        return page.map { mapToDto(it) }
    }
    
    fun searchUsers(name: String, pageable: Pageable): Page<UserDto> {
        val users = userRepository.findByFirstNameContainingOrLastNameContaining(name, name)
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, users.size)
        val pageUsers = users.subList(start, end)
        val page = PageImpl(pageUsers, pageable, users.size.toLong())
        return page.map { mapToDto(it) }
    }
    
    fun getTotalUserCount(): Long {
        return userRepository.count()
    }
    
    private fun mapToDto(user: User): UserDto {
        return UserDto(
            userId = user.userId,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            role = user.role,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}
