package com.course.studentservice.repository

import com.course.studentservice.entity.User
import com.course.studentservice.entity.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    
    fun findByEmail(email: String): Optional<User>
    
    fun findByRole(role: UserRole): List<User>
    
    fun existsByEmail(email: String): Boolean
    
    fun findByFirstNameContainingOrLastNameContaining(firstName: String, lastName: String): List<User>
}
