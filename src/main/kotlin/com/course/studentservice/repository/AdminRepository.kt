package com.course.studentservice.repository

import com.course.studentservice.entity.Admin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AdminRepository : JpaRepository<Admin, Long> {
    
    fun findByUserEmail(email: String): Optional<Admin>
    
    fun findByDepartment(department: String): List<Admin>
    
    fun findByDepartmentContaining(department: String): List<Admin>
}
